package connxt.session.service.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import connxt.session.dto.FingerprintDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TokenUtils {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();
  private static final String HMAC_ALGORITHM = "HmacSHA256";

  public static String generateToken(int bytes) {
    byte[] randomBytes = new byte[bytes];
    SECURE_RANDOM.nextBytes(randomBytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
  }

  public static String generateSessionToken() {
    // 48 bytes random => 64 chars URL-safe for stronger uniqueness
    return generateToken(48);
  }

  public static String hmacSha256(String key, String data) {
    try {
      Mac mac = Mac.getInstance(HMAC_ALGORITHM);
      SecretKeySpec secretKeySpec =
          new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
      mac.init(secretKeySpec);
      byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
      return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    } catch (Exception e) {
      log.error("Error creating HMAC hash", e);
      throw new RuntimeException("Failed to create HMAC hash", e);
    }
  }

  /** Constant-time equals to avoid timing attacks */
  public static boolean constantTimeEquals(String a, String b) {
    if (a == null || b == null) return false;
    byte[] aa = a.getBytes(StandardCharsets.UTF_8);
    byte[] bb = b.getBytes(StandardCharsets.UTF_8);
    return MessageDigest.isEqual(aa, bb);
  }

  /** HMAC over token + '|' + fingerprint canonical string */
  public static String hmacTokenWithFingerprint(
      String key, String token, FingerprintDto fingerprint) {
    String canonical = FingerprintUtils.generateCanonicalString(fingerprint);
    String combined = token + "|" + canonical;
    return hmacSha256(key, combined);
  }
}
