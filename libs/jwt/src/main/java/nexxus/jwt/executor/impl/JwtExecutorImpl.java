package nexxus.jwt.executor.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import nexxus.jwt.dto.JwtTokenRequest;
import nexxus.jwt.dto.JwtTokenResponse;
import nexxus.jwt.dto.JwtValidationRequest;
import nexxus.jwt.dto.JwtValidationResponse;
import nexxus.jwt.exception.JwtSigningKeyException;
import nexxus.jwt.exception.JwtTokenGenerationException;
import nexxus.jwt.executor.JwtExecutor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExecutorImpl implements JwtExecutor {

  @Override
  public JwtTokenResponse generateToken(JwtTokenRequest request) {
    try {
      OffsetDateTime now = OffsetDateTime.now();

      Map<String, Object> claims =
          new HashMap<>(request.getClaims() != null ? request.getClaims() : new HashMap<>());
      claims.put("token_type", request.getTokenType().getValue());

      String token =
          createJwtToken(
              request.getIssuer(),
              request.getSubject(),
              request.getAudience(),
              request.getExpiresAt() != null ? request.getExpiresAt() : now.plusHours(1),
              request.getIssuedAt() != null ? request.getIssuedAt() : now,
              claims,
              request.getSigningKeyId());

      return JwtTokenResponse.builder()
          .token(token)
          .tokenType("Bearer")
          .issuedAt(request.getIssuedAt() != null ? request.getIssuedAt() : now)
          .expiresAt(request.getExpiresAt() != null ? request.getExpiresAt() : now.plusHours(1))
          .claims(claims)
          .build();

    } catch (Exception e) {
      log.error("Failed to generate JWT token", e);
      throw new JwtTokenGenerationException("Failed to generate JWT token", e);
    }
  }

  @Override
  public JwtValidationResponse validateToken(JwtValidationRequest request) {
    try {
      Claims claims =
          verifyJwtToken(
              request.getToken(),
              request.getIssuer(),
              request.getAudience(),
              request.getSigningKeyId());

      Map<String, Object> claimsMap = new HashMap<>();
      claims.forEach((key, value) -> claimsMap.put(key, value));

      return JwtValidationResponse.builder()
          .valid(true)
          .subject(claims.getSubject())
          .issuer(claims.getIssuer())
          .audience(claims.getAudience().isEmpty() ? null : claims.getAudience().iterator().next())
          .claims(claimsMap)
          .build();

    } catch (JwtException e) {
      log.debug("JWT token validation failed", e);
      return JwtValidationResponse.builder().valid(false).errorMessage(e.getMessage()).build();
    } catch (Exception e) {
      log.error("Unexpected error during JWT token validation", e);
      return JwtValidationResponse.builder()
          .valid(false)
          .errorMessage("Token validation failed")
          .build();
    }
  }

  private String createJwtToken(
      String issuer,
      String subject,
      String audience,
      OffsetDateTime expiresAt,
      OffsetDateTime issuedAt,
      Map<String, Object> claims,
      String signingKeyId) {

    String signingKey = getSigningKey(signingKeyId);
    SecretKey key = Keys.hmacShaKeyFor(signingKey.getBytes());

    var jwtBuilder =
        Jwts.builder()
            .issuer(issuer)
            .subject(subject)
            .audience()
            .add(audience)
            .and()
            .expiration(Date.from(expiresAt.toInstant()))
            .issuedAt(Date.from(issuedAt.toInstant()));

    if (claims != null) {
      for (Map.Entry<String, Object> entry : claims.entrySet()) {
        String claimKey = entry.getKey();
        Object value = entry.getValue();

        if (value instanceof String) {
          jwtBuilder.claim(claimKey, (String) value);
        } else if (value instanceof Integer) {
          jwtBuilder.claim(claimKey, (Integer) value);
        } else if (value instanceof Long) {
          jwtBuilder.claim(claimKey, (Long) value);
        } else if (value instanceof Boolean) {
          jwtBuilder.claim(claimKey, (Boolean) value);
        } else if (value instanceof OffsetDateTime) {
          jwtBuilder.claim(claimKey, ((OffsetDateTime) value).toInstant());
        } else if (value != null) {
          jwtBuilder.claim(claimKey, value.toString());
        }
        // Skip null values - don't add them as claims
      }
    }

    return jwtBuilder.signWith(key).compact();
  }

  private Claims verifyJwtToken(String token, String issuer, String audience, String signingKeyId) {
    String signingKey = getSigningKey(signingKeyId);
    SecretKey key = Keys.hmacShaKeyFor(signingKey.getBytes());

    return Jwts.parser()
        .verifyWith(key)
        .requireIssuer(issuer)
        .requireAudience(audience)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  private String getSigningKey(String keyId) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      String keySource = "jwt-signing-key-" + keyId + "-nexxus-token-system";
      byte[] keyBytes = digest.digest(keySource.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(keyBytes);
    } catch (Exception e) {
      log.error("Failed to get JWT signing key", e);
      throw new JwtSigningKeyException("Failed to get JWT signing key", e);
    }
  }
}
