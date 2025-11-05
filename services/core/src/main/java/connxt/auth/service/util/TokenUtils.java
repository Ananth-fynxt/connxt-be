package connxt.auth.service.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TokenUtils {

  private static final String HMAC_ALGORITHM = "HmacSHA256";

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
}
