package nexxus.shared.util;

import java.security.SecureRandom;

public class RandomIdGenerator {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();
  private static final String ALPHANUMERIC_CHARS =
      "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final int DEFAULT_LENGTH = 26;

  protected String generateId(String prefix) {
    return (prefix == null || prefix.isEmpty() ? "" : prefix + "_") + generateRandomId();
  }

  protected String generateId(String prefix, int length) {
    return (prefix == null || prefix.isEmpty() ? "" : prefix) + generateRandomId(length);
  }

  protected String generateRandomId() {
    return generateRandomId(DEFAULT_LENGTH);
  }

  protected String generateRandomId(int length) {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int randomIndex = SECURE_RANDOM.nextInt(ALPHANUMERIC_CHARS.length());
      sb.append(ALPHANUMERIC_CHARS.charAt(randomIndex));
    }
    return sb.toString();
  }
}
