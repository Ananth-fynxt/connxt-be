package connxt.auth.service.impl;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import connxt.auth.dto.AuthResponse;
import connxt.auth.dto.LoginRequest;
import connxt.auth.dto.UserInfo;
import connxt.auth.service.AuthService;
import connxt.jwt.dto.JwtTokenRequest;
import connxt.jwt.dto.JwtTokenResponse;
import connxt.jwt.dto.JwtValidationRequest;
import connxt.jwt.dto.JwtValidationResponse;
import connxt.jwt.exception.JwtSigningKeyException;
import connxt.jwt.exception.JwtTokenGenerationException;
import connxt.jwt.executor.JwtExecutor;
import connxt.shared.constants.AuthType;
import connxt.shared.constants.ErrorCode;
import connxt.shared.constants.TokenType;

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
    claims.put("user_id", user.getUserId());
    claims.put("scope", user.getScope().getValue());
    claims.put("token_type", TokenType.ACCESS.getValue());
    claims.put("email", user.getEmail());
    claims.put("roleId", user.getRoleId());
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

  private JwtTokenResponse generateAccessTokenFromRefreshToken(
      String authType, JwtValidationResponse validationResult) {
    // Only APPLICATION_USER auth type is supported (system users)
    if (!AuthType.APPLICATION_USER.getValue().equals(authType)) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, ErrorCode.TOKEN_VALIDATION_FAILED.getCode());
    }

    String userId = (String) validationResult.getClaims().get("user_id");
    UserInfo user = userAuthService.getUserInfoById(userId);
    return generateUserToken(user);
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
      String userId = (String) result.getClaims().get("user_id");
      if (userId != null) {
        return userId;
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
