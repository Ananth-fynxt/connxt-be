package connxt.user.util;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PasswordUtil {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();
  private static final String ALPHANUMERIC_CHARS =
      "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

  public String generateStrongPassword(int length) {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int randomIndex = SECURE_RANDOM.nextInt(ALPHANUMERIC_CHARS.length());
      sb.append(ALPHANUMERIC_CHARS.charAt(randomIndex));
    }
    String password = sb.toString();
    log.debug("Generated password of length: {}", length);
    return password;
  }

  public String generateStrongPassword() {
    return generateStrongPassword(16);
  }
}
