package nexxus.auth.service.impl;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import nexxus.auth.dto.AuthResponse;
import nexxus.auth.dto.CustomerInfo;
import nexxus.auth.dto.ExternalLoginRequest;
import nexxus.auth.dto.LoginRequest;
import nexxus.auth.dto.UserInfo;
import nexxus.auth.service.AuthService;
import nexxus.jwt.dto.JwtTokenRequest;
import nexxus.jwt.dto.JwtTokenResponse;
import nexxus.jwt.dto.JwtValidationRequest;
import nexxus.jwt.dto.JwtValidationResponse;
import nexxus.jwt.exception.JwtSigningKeyException;
import nexxus.jwt.exception.JwtTokenGenerationException;
import nexxus.jwt.executor.JwtExecutor;
import nexxus.shared.constants.AuthType;
import nexxus.shared.constants.ErrorCode;
import nexxus.shared.constants.TokenType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final JwtExecutor jwtExecutor;
  private final UserAuthenticationService userAuthService;
  private final TokenManagementService tokenManagementService;

  @Value("${security.jwt.issuer}")
  private String jwtIssuer;

  @Value("${security.jwt.audience}")
  private String jwtAudience;

  @Value("${security.jwt.signing-key-id}")
  private String signingKeyId;

  @Value("${security.jwt.refresh-signing-key-id}")
  private String refreshSigningKeyId;

  @Value("${security.jwt.access-token-expiration}")
  private Duration accessTokenExpiration;

  @Value("${security.jwt.refresh-token-expiration}")
  private Duration refreshTokenExpiration;

  @Override
  @Transactional
  public AuthResponse login(LoginRequest request) {
    log.info("Attempting login");

    var user = userAuthService.authenticateUser(request.getEmail(), request.getPassword());
    if (user == null) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, ErrorCode.AUTH_INVALID_CREDENTIALS.getCode());
    }

    try {
      JwtTokenResponse accessTokenResponse = generateUserToken(user);
      JwtTokenResponse refreshTokenResponse = generateUserRefreshToken(user);

      tokenManagementService.cleanupExpiredTokensForUser(user.getUserId());

      tokenManagementService.saveTokens(
          accessTokenResponse, refreshTokenResponse, user.getUserId());

      log.info("Login successful");
      return buildAuthResponse(accessTokenResponse, refreshTokenResponse.getToken());
    } catch (JwtTokenGenerationException | JwtSigningKeyException e) {
      log.error("Failed to generate token for user login", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.TOKEN_ISSUANCE_FAILED.getCode());
    }
  }

  @Override
  @Transactional
  public AuthResponse externalLogin(ExternalLoginRequest request) {
    log.info("Attempting external login");

    var customer =
        userAuthService.authenticateCustomer(request.getCustomerId(), request.getSecretToken());
    if (customer == null) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, ErrorCode.AUTH_INVALID_CREDENTIALS.getCode());
    }

    try {
      JwtTokenResponse accessTokenResponse = generateExternalApiAccessToken(customer);
      JwtTokenResponse refreshTokenResponse = generateExternalApiRefreshToken(customer);

      tokenManagementService.cleanupExpiredTokensForUser(customer.getCustomerId());

      tokenManagementService.saveTokens(
          accessTokenResponse, refreshTokenResponse, customer.getCustomerId());

      log.info("External login successful");
      return buildAuthResponse(accessTokenResponse, refreshTokenResponse.getToken());
    } catch (JwtTokenGenerationException | JwtSigningKeyException e) {
      log.error("Failed to generate token for external login", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.TOKEN_ISSUANCE_FAILED.getCode());
    }
  }

  @Override
  @Transactional
  public AuthResponse refreshToken(String refreshToken) {
    log.info("Refreshing token");

    try {
      JwtValidationResponse validationResult = validateRefreshToken(refreshToken);
      String authType = (String) validationResult.getClaims().get("auth_type");
      String subject = validationResult.getSubject();

      JwtTokenResponse newTokenResponse =
          generateAccessTokenFromRefreshToken(authType, validationResult);

      tokenManagementService.revokeAccessTokensForUser(subject);
      tokenManagementService.saveToken(newTokenResponse, subject);

      log.info("Token refreshed successfully");
      return buildAuthResponse(newTokenResponse, refreshToken);
    } catch (JwtTokenGenerationException | JwtSigningKeyException e) {
      log.error("Failed to refresh token", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.TOKEN_VALIDATION_FAILED.getCode());
    }
  }

  @Override
  @Transactional
  public String logout(String authorization) {
    log.info("Logging out user");

    String token = authorization.replace("Bearer ", "");
    log.info("Token: {}", token);
    String userId = extractUserId(token);
    if (userId != null) {
      tokenManagementService.revokeAllTokensForUser(userId);
      return "Logout successful";
    } else {
      return "Logout failed";
    }
  }

  private JwtTokenResponse generateUserToken(UserInfo user) {
    Map<String, Object> claims = buildUserAccessTokenClaims(user);

    JwtTokenRequest jwtRequest =
        JwtTokenRequest.builder()
            .issuer(jwtIssuer)
            .audience(jwtAudience)
            .subject(user.getUserId())
            .expiresAt(OffsetDateTime.now().plus(accessTokenExpiration))
            .claims(claims)
            .signingKeyId(signingKeyId)
            .tokenType(TokenType.ACCESS)
            .build();

    return jwtExecutor.generateToken(jwtRequest);
  }

  private JwtTokenResponse generateUserRefreshToken(UserInfo user) {
    Map<String, Object> claims = buildUserRefreshTokenClaims(user);

    JwtTokenRequest jwtRequest =
        JwtTokenRequest.builder()
            .issuer(jwtIssuer)
            .audience(jwtAudience)
            .subject(user.getUserId())
            .expiresAt(OffsetDateTime.now().plus(refreshTokenExpiration))
            .claims(claims)
            .signingKeyId(refreshSigningKeyId)
            .tokenType(TokenType.REFRESH)
            .build();

    return jwtExecutor.generateToken(jwtRequest);
  }

  private Map<String, Object> buildUserAccessTokenClaims(UserInfo user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("auth_type", AuthType.APPLICATION_USER.getValue());
    claims.put("token_type", TokenType.ACCESS.getValue());
    claims.put("scope", user.getScope().getValue());
    claims.put("user_id", user.getUserId());
    claims.put("email", user.getEmail());
    claims.put("fi_id", user.getFiId());
    claims.put("fi_name", user.getFiName());
    claims.put("brands", user.getBrands());
    claims.put("accessible_brands", user.getAccessibleBrands());
    return claims;
  }

  private Map<String, Object> buildUserRefreshTokenClaims(UserInfo user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("auth_type", AuthType.APPLICATION_USER.getValue());
    claims.put("token_type", TokenType.REFRESH.getValue());
    claims.put("user_id", user.getUserId());
    claims.put("scope", user.getScope().getValue());
    return claims;
  }

  private JwtTokenResponse generateExternalApiAccessToken(CustomerInfo customer) {
    Map<String, Object> claims = buildExternalApiClaims(customer, TokenType.ACCESS.getValue());

    JwtTokenRequest jwtRequest =
        JwtTokenRequest.builder()
            .issuer(jwtIssuer)
            .audience(jwtAudience)
            .subject(customer.getCustomerId())
            .expiresAt(OffsetDateTime.now().plus(accessTokenExpiration))
            .claims(claims)
            .signingKeyId(signingKeyId)
            .tokenType(TokenType.ACCESS)
            .build();

    return jwtExecutor.generateToken(jwtRequest);
  }

  private JwtTokenResponse generateExternalApiRefreshToken(CustomerInfo customer) {
    Map<String, Object> claims = buildExternalApiClaims(customer, TokenType.REFRESH.getValue());

    JwtTokenRequest jwtRequest =
        JwtTokenRequest.builder()
            .issuer(jwtIssuer)
            .audience(jwtAudience)
            .subject(customer.getCustomerId())
            .expiresAt(OffsetDateTime.now().plus(refreshTokenExpiration))
            .claims(claims)
            .signingKeyId(refreshSigningKeyId)
            .tokenType(TokenType.REFRESH)
            .build();

    return jwtExecutor.generateToken(jwtRequest);
  }

  private Map<String, Object> buildExternalApiClaims(CustomerInfo customer, String tokenType) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("auth_type", AuthType.EXTERNAL_API.getValue());
    claims.put("token_type", tokenType);
    claims.put("scope", customer.getScope().getValue());
    claims.put("environment_id", customer.getEnvironmentId());
    claims.put("brand_id", customer.getBrandId());
    claims.put("customer_id", customer.getCustomerId());
    return claims;
  }

  private JwtTokenResponse generateAccessTokenFromRefreshToken(
      String authType, JwtValidationResponse validationResult) {
    if (AuthType.APPLICATION_USER.getValue().equals(authType)) {
      return generateAccessTokenForApplicationUser(validationResult);
    } else if (AuthType.EXTERNAL_API.getValue().equals(authType)) {
      return generateAccessTokenForExternalApi(validationResult);
    } else {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, ErrorCode.TOKEN_VALIDATION_FAILED.getCode());
    }
  }

  private JwtTokenResponse generateAccessTokenForApplicationUser(
      JwtValidationResponse validationResult) {
    String userId = (String) validationResult.getClaims().get("user_id");
    UserInfo user = userAuthService.getUserInfoById(userId);
    return generateUserToken(user);
  }

  private JwtTokenResponse generateAccessTokenForExternalApi(
      JwtValidationResponse validationResult) {
    String customerId = (String) validationResult.getClaims().get("customer_id");
    String brandId = (String) validationResult.getClaims().get("brand_id");
    String environmentId = (String) validationResult.getClaims().get("environment_id");

    CustomerInfo customer = userAuthService.getCustomerInfoById(customerId, brandId, environmentId);
    return generateExternalApiAccessToken(customer);
  }

  private JwtValidationResponse validateRefreshToken(String refreshToken) {
    JwtValidationRequest validationRequest =
        JwtValidationRequest.builder()
            .token(refreshToken)
            .issuer(jwtIssuer)
            .audience(jwtAudience)
            .signingKeyId(refreshSigningKeyId)
            .build();

    JwtValidationResponse validationResult = jwtExecutor.validateToken(validationRequest);
    if (!validationResult.isValid()) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, ErrorCode.TOKEN_VALIDATION_FAILED.getCode());
    }

    String tokenType = (String) validationResult.getClaims().get("token_type");
    if (!TokenType.REFRESH.getValue().equals(tokenType)) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, ErrorCode.TOKEN_VALIDATION_FAILED.getCode());
    }

    String subject = validationResult.getSubject();
    if (!tokenManagementService.isRefreshTokenActive(refreshToken, subject)) {
      log.warn("Attempted to use revoked refresh token");
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, ErrorCode.TOKEN_VALIDATION_FAILED.getCode());
    }

    return validationResult;
  }

  private String extractUserId(String token) {
    JwtValidationRequest request =
        JwtValidationRequest.builder()
            .token(token)
            .issuer(jwtIssuer)
            .audience(jwtAudience)
            .signingKeyId(signingKeyId)
            .build();

    JwtValidationResponse result = jwtExecutor.validateToken(request);
    if (result.isValid()) {
      // For regular user logins, use user_id
      String userId = (String) result.getClaims().get("user_id");
      if (userId != null) {
        return userId;
      }

      // For external API logins, use customer_id
      String customerId = (String) result.getClaims().get("customer_id");
      if (customerId != null) {
        return customerId;
      }
    }
    return null;
  }

  private AuthResponse buildAuthResponse(
      JwtTokenResponse accessTokenResponse, String refreshToken) {

    Map<String, Object> claims = new HashMap<>();
    claims.putAll(accessTokenResponse.getClaims());

    return AuthResponse.builder()
        .accessToken(accessTokenResponse.getToken())
        .refreshToken(refreshToken)
        .tokenType(accessTokenResponse.getTokenType())
        .issuedAt(accessTokenResponse.getIssuedAt())
        .expiresAt(accessTokenResponse.getExpiresAt())
        .claims(claims)
        .build();
  }
}
