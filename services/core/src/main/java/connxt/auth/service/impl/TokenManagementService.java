package connxt.auth.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import connxt.auth.entity.Token;
import connxt.auth.repository.TokenRepository;
import connxt.auth.service.util.TokenUtils;
import connxt.jwt.dto.JwtTokenResponse;
import connxt.shared.constants.ErrorCode;
import connxt.shared.constants.TokenStatus;
import connxt.shared.constants.TokenType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenManagementService {

  private final TokenRepository tokenRepository;

  @Transactional
  public void saveToken(JwtTokenResponse tokenResponse, String customerId) {
    String tokenHash =
        generateTokenHash(tokenResponse.getToken(), customerId, tokenResponse.getExpiresAt());

    Token token =
        Token.builder()
            .customerId(customerId)
            .tokenHash(tokenHash)
            .issuedAt(tokenResponse.getIssuedAt())
            .expiresAt(tokenResponse.getExpiresAt())
            .status(TokenStatus.ACTIVE)
            .tokenType(extractTokenType(tokenResponse.getClaims()))
            .build();

    tokenRepository.save(token);
    log.debug("Token saved to database");
  }

  @Transactional
  public void saveTokens(
      JwtTokenResponse accessToken, JwtTokenResponse refreshToken, String customerId) {
    String accessTokenHash =
        generateTokenHash(accessToken.getToken(), customerId, accessToken.getExpiresAt());

    String refreshTokenHash =
        generateTokenHash(refreshToken.getToken(), customerId, refreshToken.getExpiresAt());

    Token accessTokenEntity =
        Token.builder()
            .customerId(customerId)
            .tokenHash(accessTokenHash)
            .issuedAt(accessToken.getIssuedAt())
            .expiresAt(accessToken.getExpiresAt())
            .status(TokenStatus.ACTIVE)
            .tokenType(TokenType.ACCESS)
            .build();

    Token refreshTokenEntity =
        Token.builder()
            .customerId(customerId)
            .tokenHash(refreshTokenHash)
            .issuedAt(refreshToken.getIssuedAt())
            .expiresAt(refreshToken.getExpiresAt())
            .status(TokenStatus.ACTIVE)
            .tokenType(TokenType.REFRESH)
            .build();

    tokenRepository.save(accessTokenEntity);
    tokenRepository.save(refreshTokenEntity);

    log.debug("Tokens saved");
  }

  private TokenType extractTokenType(Map<String, Object> claims) {
    String tokenTypeValue = (String) claims.get("token_type");
    if (TokenType.ACCESS.getValue().equals(tokenTypeValue)) {
      return TokenType.ACCESS;
    } else if (TokenType.REFRESH.getValue().equals(tokenTypeValue)) {
      return TokenType.REFRESH;
    }
    return TokenType.ACCESS;
  }

  @Transactional
  public void revokeToken(String tokenHash) {
    tokenRepository
        .findById(tokenHash)
        .ifPresent(
            token -> {
              token.setStatus(TokenStatus.REVOKED);
              tokenRepository.save(token);
              log.debug("Token revoked");
            });
  }

  private String generateTokenHash(String token, String customerId, OffsetDateTime expiresAt) {
    OffsetDateTime normalizedExpiresAt =
        expiresAt
            .withOffsetSameInstant(ZoneOffset.UTC)
            .truncatedTo(java.time.temporal.ChronoUnit.SECONDS);

    log.debug("Token hash generated");
    String data = String.format("%s|%s|%s", token, customerId, normalizedExpiresAt.toString());
    return TokenUtils.hmacSha256(getBindingKey(), data);
  }

  public String generateTokenHashFromToken(String token, String subject) {
    try {
      String[] parts = token.split("\\.");
      if (parts.length == 3) {
        String payload = parts[1];
        while (payload.length() % 4 != 0) {
          payload += "=";
        }
        byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
        String payloadJson = new String(decodedBytes);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(payloadJson);
        long exp = jsonNode.get("exp").asLong();

        OffsetDateTime expiresAt =
            OffsetDateTime.ofInstant(Instant.ofEpochSecond(exp), ZoneOffset.UTC);

        log.debug("JWT expiration extracted");
        return generateTokenHash(token, subject, expiresAt);
      }
      return null;
    } catch (Exception e) {
      log.error("Failed to generate token hash from token", e);
      return null;
    }
  }

  public String getBindingKey() {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      String keySource = "binding-key-ananth-connxt-token-system";
      byte[] keyBytes = digest.digest(keySource.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(keyBytes);
    } catch (Exception e) {
      log.error("Failed to get binding key", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.TOKEN_SECRET_GENERATION_FAILED.getCode());
    }
  }

  @Transactional
  public void revokeRefreshTokensForUser(String customerId) {
    try {
      tokenRepository
          .findActiveByCustomerIdAndTokenType(customerId, TokenType.REFRESH, TokenStatus.ACTIVE)
          .forEach(
              token -> {
                token.setStatus(TokenStatus.REVOKED);
                tokenRepository.save(token);
              });
      log.debug("Refresh tokens revoked for user");
    } catch (Exception e) {
      log.error("Failed to revoke refresh tokens for user: {}", customerId, e);
    }
  }

  @Transactional
  public void revokeAllTokensForUser(String customerId) {
    try {
      tokenRepository
          .findActiveByCustomerId(customerId, TokenStatus.ACTIVE)
          .forEach(
              token -> {
                token.setStatus(TokenStatus.REVOKED);
                tokenRepository.save(token);
                log.debug("Token revoked for user");
              });
      log.info("All tokens revoked for user");
    } catch (Exception e) {
      log.error("Failed to revoke tokens for user: {}", customerId, e);
    }
  }

  @Transactional
  public void revokeAccessTokensForUser(String customerId) {
    try {
      tokenRepository
          .findActiveByCustomerIdAndTokenType(customerId, TokenType.ACCESS, TokenStatus.ACTIVE)
          .forEach(
              token -> {
                token.setStatus(TokenStatus.REVOKED);
                tokenRepository.save(token);
                log.debug("Access token revoked for user");
              });
      log.debug("All access tokens revoked for user");
    } catch (Exception e) {
      log.error("Failed to revoke access tokens for user: {}", customerId, e);
    }
  }

  public boolean isRefreshTokenActive(String refreshToken, String subject) {
    try {
      String refreshTokenHash = generateTokenHashFromToken(refreshToken, subject);
      if (refreshTokenHash == null) {
        log.warn("Failed to generate hash from refresh token");
        return false;
      }

      log.debug("Refresh token hash generated");

      Long count =
          tokenRepository.countActiveToken(refreshTokenHash, TokenStatus.ACTIVE, TokenType.REFRESH);

      log.debug("Database validation completed");
      return count != null && count > 0;
    } catch (Exception e) {
      log.error("Failed to validate refresh token status", e);
      return false;
    }
  }

  public boolean isAccessTokenActive(String tokenHash) {
    try {
      Long count =
          tokenRepository.countActiveToken(tokenHash, TokenStatus.ACTIVE, TokenType.ACCESS);

      log.debug("Access token database validation completed");
      return count != null && count > 0;
    } catch (Exception e) {
      log.error("Failed to validate access token status", e);
      return false;
    }
  }

  @Transactional
  public void updateExpiredTokenStatus(String tokenHash) {
    try {
      Optional<Token> tokenOpt = tokenRepository.findByTokenHash(tokenHash);
      if (tokenOpt.isPresent()) {
        Token token = tokenOpt.get();

        // Check if token is expired but still marked as ACTIVE
        if (token.getStatus() == TokenStatus.ACTIVE
            && token.getExpiresAt().isBefore(OffsetDateTime.now())) {

          token.setStatus(TokenStatus.EXPIRED);
          tokenRepository.save(token);

          log.debug("Updated expired token status for hash: {}", tokenHash);
        }
      }
    } catch (Exception e) {
      log.error("Failed to update expired token status for hash: {}", tokenHash, e);
    }
  }

  @Transactional
  public void cleanupExpiredTokensForUser(String customerId) {
    try {
      OffsetDateTime now = OffsetDateTime.now();
      List<Token> expiredTokens =
          tokenRepository.findExpiredTokens(now, TokenStatus.ACTIVE).stream()
              .filter(token -> customerId.equals(token.getCustomerId()))
              .toList();

      if (!expiredTokens.isEmpty()) {
        expiredTokens.forEach(
            token -> {
              token.setStatus(TokenStatus.EXPIRED);
              tokenRepository.save(token);
            });
        log.debug("Expired tokens cleaned up for user");
      }
    } catch (Exception e) {
      log.error("Failed to cleanup expired tokens for user: {}", customerId, e);
    }
  }
}
