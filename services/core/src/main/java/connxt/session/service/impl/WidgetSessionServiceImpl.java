package connxt.session.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import connxt.brandcustomer.service.BrandCustomerService;
import connxt.session.dto.FingerprintDto;
import connxt.session.dto.SessionDto;
import connxt.session.entity.WidgetSession;
import connxt.session.repository.WidgetSessionRepository;
import connxt.session.service.WidgetSessionService;
import connxt.session.service.mappers.WidgetSessionMapper;
import connxt.session.service.util.FingerprintUtils;
import connxt.session.service.util.TokenUtils;
import connxt.shared.constants.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WidgetSessionServiceImpl implements WidgetSessionService {

  private final WidgetSessionRepository widgetSessionRepository;
  private final WidgetSessionMapper widgetSessionMapper;
  private final BrandCustomerService brandCustomerService;

  @Value("${widget.session.hmac.key}")
  private String hmacKey;

  @Value("${widget.session.default.timeout-minutes}")
  private Integer defaultTimeoutMinutes;

  @Value("${widget.session.default.auto-extend}")
  private Boolean defaultAutoExtend;

  @Value("${widget.session.fingerprint.similarity-threshold}")
  private Double fingerprintSimilarityThreshold;

  @Override
  @Transactional
  public SessionDto.CreateResponse create(SessionDto.CreateRequest requestDto) {
    log.info(
        "Creating session for customer: {}, brand: {}, environment: {}",
        requestDto.getCustomerId(),
        requestDto.getBrandId(),
        requestDto.getEnvironmentId());

    // Validate brand customer combination exists
    if (!brandCustomerService.validateBrandCustomerExists(
        requestDto.getBrandId(), requestDto.getEnvironmentId(), requestDto.getCustomerId())) {
      log.warn(
          "Invalid brand customer combination - brand: {}, environment: {}, customer: {}",
          requestDto.getBrandId(),
          requestDto.getEnvironmentId(),
          requestDto.getCustomerId());
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, ErrorCode.BRAND_CUSTOMER_INVALID_COMBINATION.getCode());
    }

    // Validate fingerprint is provided
    if (requestDto.getDeviceInfo() == null || requestDto.getDeviceInfo().getFingerprint() == null) {
      log.warn("Fingerprint is required for session creation");
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, ErrorCode.SESSION_FINGERPRINT_REQUIRED.getCode());
    }

    // Validate fingerprint has required fields
    FingerprintDto fingerprint = requestDto.getDeviceInfo().getFingerprint();
    if (!FingerprintUtils.isValidFingerprint(fingerprint)) {
      log.warn("Invalid fingerprint provided - missing required fields");
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, ErrorCode.SESSION_FINGERPRINT_REQUIRED.getCode());
    }

    // Generate new session token and hash
    String sessionToken = TokenUtils.generateSessionToken();
    String sessionTokenHash = TokenUtils.hmacSha256(hmacKey, sessionToken);

    // Compute fingerprint binding hash
    String fingerprintHash =
        TokenUtils.hmacTokenWithFingerprint(hmacKey, sessionToken, fingerprint);

    // Use default values if not provided by client
    Integer timeoutMinutes =
        requestDto.getSessionConfig().getTimeoutMinutes() != null
            ? requestDto.getSessionConfig().getTimeoutMinutes()
            : defaultTimeoutMinutes;

    Boolean autoExtend =
        requestDto.getSessionConfig().getAutoExtend() != null
            ? requestDto.getSessionConfig().getAutoExtend()
            : defaultAutoExtend;

    // Calculate expiry time
    Instant expiresAt = widgetSessionMapper.calculateExpiryTime(timeoutMinutes);

    // Create session entity with resolved values
    WidgetSession session =
        widgetSessionMapper.toWidgetSessionWithDefaults(
            requestDto, sessionTokenHash, fingerprintHash, expiresAt, timeoutMinutes, autoExtend);

    // Save session
    WidgetSession savedSession = widgetSessionRepository.save(session);

    log.info("Session created successfully with ID: {}", savedSession.getId());

    return widgetSessionMapper.toSessionCreateResponseDto(savedSession, sessionToken);
  }

  @Override
  @Transactional
  public SessionDto.RefreshResponse refresh(String currentToken, FingerprintDto fingerprint) {
    log.debug("Refreshing session with token");

    // Find session by token hash
    String tokenHash = TokenUtils.hmacSha256(hmacKey, currentToken);
    WidgetSession session =
        widgetSessionRepository
            .findBySessionTokenHash(tokenHash)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, ErrorCode.SESSION_NOT_FOUND.getCode()));

    // Validate fingerprint binding with similarity check
    String expectedBinding =
        TokenUtils.hmacTokenWithFingerprint(hmacKey, currentToken, fingerprint);
    boolean exactMatch =
        TokenUtils.constantTimeEquals(expectedBinding, session.getFingerprintHash());

    if (!exactMatch) {
      // Check if fingerprints are similar enough to allow refresh
      FingerprintDto storedFingerprint =
          widgetSessionMapper.toFingerprintDto(session.getFingerprint());

      boolean isSimilar =
          FingerprintUtils.isFingerprintSimilar(
              fingerprint, storedFingerprint, fingerprintSimilarityThreshold);

      if (!isSimilar) {
        log.warn(
            "Fingerprint mismatch during session refresh for session ID: {}. Similarity check failed.",
            session.getId());
        throw new ResponseStatusException(
            HttpStatus.UNAUTHORIZED, ErrorCode.SESSION_FINGERPRINT_MISMATCH.getCode());
      }

      // Log anomaly for similar but not exact fingerprints
      boolean ipAnomaly = FingerprintUtils.detectIpAnomaly(fingerprint, storedFingerprint);
      if (ipAnomaly) {
        log.warn(
            "IP address anomaly detected during session refresh for session ID: {}. IP changed from {} to {}",
            session.getId(),
            storedFingerprint.getIpAddress(),
            fingerprint.getIpAddress());
      }

      log.info(
          "Fingerprint similarity allowed session refresh for session ID: {}", session.getId());
    }

    // Validate session
    validateSessionForRefresh(session);

    // Generate new token and hash
    String newToken = TokenUtils.generateSessionToken();
    String newTokenHash = TokenUtils.hmacSha256(hmacKey, newToken);

    // Update fingerprint binding with new token
    String newFingerprintHash = TokenUtils.hmacTokenWithFingerprint(hmacKey, newToken, fingerprint);

    // Calculate new expiry time
    Instant newExpiresAt = Instant.now().plus(session.getTimeoutMinutes(), ChronoUnit.MINUTES);

    // Update session
    widgetSessionMapper.updateSessionForRefresh(
        session, newTokenHash, newFingerprintHash, newExpiresAt);
    WidgetSession updatedSession = widgetSessionRepository.save(session);

    log.info(
        "Session refreshed successfully for ID: {}, extension count: {}",
        updatedSession.getId(),
        updatedSession.getExtensionCount());

    return widgetSessionMapper.toSessionRefreshResponseDto(updatedSession, newToken);
  }

  @Override
  @Transactional
  public SessionDto.ValidateResponse validate(String token, FingerprintDto fingerprint) {
    log.debug("Validating session token");

    String tokenHash = TokenUtils.hmacSha256(hmacKey, token);
    WidgetSession session =
        widgetSessionRepository
            .findBySessionTokenHash(tokenHash)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, ErrorCode.SESSION_NOT_FOUND.getCode()));

    // Validate fingerprint binding with similarity check
    String expectedBinding = TokenUtils.hmacTokenWithFingerprint(hmacKey, token, fingerprint);
    boolean exactMatch =
        TokenUtils.constantTimeEquals(expectedBinding, session.getFingerprintHash());

    if (!exactMatch) {
      // Check if fingerprints are similar enough to allow validation
      FingerprintDto storedFingerprint =
          widgetSessionMapper.toFingerprintDto(session.getFingerprint());

      boolean isSimilar =
          FingerprintUtils.isFingerprintSimilar(
              fingerprint, storedFingerprint, fingerprintSimilarityThreshold);

      if (!isSimilar) {
        log.warn(
            "Fingerprint mismatch during session validation for session ID: {}. Similarity check failed.",
            session.getId());
        throw new ResponseStatusException(
            HttpStatus.UNAUTHORIZED, ErrorCode.SESSION_FINGERPRINT_MISMATCH.getCode());
      }

      // Log anomaly for similar but not exact fingerprints
      boolean ipAnomaly = FingerprintUtils.detectIpAnomaly(fingerprint, storedFingerprint);
      if (ipAnomaly) {
        log.warn(
            "IP address anomaly detected during session validation for session ID: {}. IP changed from {} to {}",
            session.getId(),
            storedFingerprint.getIpAddress(),
            fingerprint.getIpAddress());
      }

      log.info(
          "Fingerprint similarity allowed session validation for session ID: {}", session.getId());
    }

    // Check if session is expired
    if (Instant.now().isAfter(session.getExpiresAt())) {
      log.warn("Session validation failed - session expired for session ID: {}", session.getId());
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, ErrorCode.SESSION_EXPIRED.getCode());
    }

    // Check if session is revoked
    if (session.getRevoked()) {
      log.warn("Session validation failed - session revoked for session ID: {}", session.getId());
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, ErrorCode.SESSION_REVOKED.getCode());
    }

    // Update last accessed time
    session.setLastAccessedAt(Instant.now());
    widgetSessionRepository.save(session);

    return widgetSessionMapper.toSessionValidateResponseDto(session);
  }

  @Override
  @Transactional
  public void revoke(SessionDto.RevokeRequest requestDto) {
    log.info("Revoking session with ID: {}", requestDto.getSessionId());

    WidgetSession session =
        widgetSessionRepository
            .findById(requestDto.getSessionId())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.SESSION_NOT_FOUND.getCode()));

    session.setRevoked(true);
    session.setRevokedBy(requestDto.getRevokedBy() != null ? requestDto.getRevokedBy() : "system");
    session.setRevokedAt(Instant.now());
    widgetSessionRepository.save(session);

    log.info(
        "Session revoked successfully with ID: {} by {}",
        requestDto.getSessionId(),
        session.getRevokedBy());
  }

  @Override
  @Transactional
  public void revokeAll(String customerId, String brandId, String environmentId) {
    log.info(
        "Revoking all sessions for customer: {}, brand: {}, environment: {}",
        customerId,
        brandId,
        environmentId);

    if (!brandCustomerService.validateBrandCustomerExists(brandId, environmentId, customerId)) {
      log.warn(
          "Invalid brand customer combination - brand: {}, environment: {}, customer: {}",
          brandId,
          environmentId,
          customerId);
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, ErrorCode.BRAND_CUSTOMER_INVALID_COMBINATION.getCode());
    }

    widgetSessionRepository.revokeSessionsByCustomerAndBrandAndEnvironment(
        customerId, brandId, environmentId);

    log.info("All sessions revoked successfully for customer: {}", customerId);
  }

  private void validateSessionForRefresh(WidgetSession session) {
    // Check if session is revoked
    if (session.getRevoked()) {
      log.warn("Session refresh failed - session revoked for session ID: {}", session.getId());
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, ErrorCode.SESSION_REVOKED.getCode());
    }

    // Check if session is expired
    if (Instant.now().isAfter(session.getExpiresAt()) && !session.getAutoExtend()) {
      log.warn("Session refresh failed - session expired for session ID: {}", session.getId());
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, ErrorCode.SESSION_EXPIRED.getCode());
    }

    // Check if auto-extend is disabled
    if (!session.getAutoExtend()) {
      log.warn("Session refresh failed - auto-extend disabled for session ID: {}", session.getId());
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, ErrorCode.SESSION_AUTO_EXTEND_DISABLED.getCode());
    }

    // Check if max extensions reached
    if (session.getExtensionCount() >= session.getMaxExtensions()) {
      log.warn(
          "Session refresh failed - max extensions reached ({}/{}) for session ID: {}",
          session.getExtensionCount(),
          session.getMaxExtensions(),
          session.getId());
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, ErrorCode.SESSION_MAX_EXTENSIONS_REACHED.getCode());
    }
  }
}
