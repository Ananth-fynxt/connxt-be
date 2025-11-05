package connxt.session.service.util;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FingerprintEncryptionUtils {

  private static final String ALGORITHM = "AES";
  private static final String TRANSFORMATION = "AES/GCM/NoPadding";
  private static final int GCM_IV_LENGTH = 12; // 96 bits
  private static final int GCM_TAG_LENGTH = 16; // 128 bits

  // Default encryption key - should be overridden in production (exactly 32 bytes for AES-256)
  private static final String DEFAULT_ENCRYPTION_KEY = "default-encryption-key-32-bytes!";

  private static String encryptionKey =
      System.getProperty(
          "widget.session.fingerprint.encryption.key",
          System.getenv()
              .getOrDefault("WIDGET_SESSION_FINGERPRINT_ENCRYPTION_KEY", DEFAULT_ENCRYPTION_KEY));

  /** Encrypt fingerprint data for storage at rest. Uses AES-GCM for authenticated encryption. */
  public static String encryptFingerprint(String fingerprintJson) {
    if (fingerprintJson == null || fingerprintJson.isEmpty()) {
      return null;
    }

    try {
      // Generate random IV
      byte[] iv = new byte[GCM_IV_LENGTH];
      new SecureRandom().nextBytes(iv);

      // Create secret key from configured key
      SecretKey secretKey =
          new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);

      // Initialize cipher
      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
      cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

      // Encrypt data
      byte[] encryptedData = cipher.doFinal(fingerprintJson.getBytes(StandardCharsets.UTF_8));

      // Combine IV and encrypted data
      byte[] encryptedWithIv = new byte[GCM_IV_LENGTH + encryptedData.length];
      System.arraycopy(iv, 0, encryptedWithIv, 0, GCM_IV_LENGTH);
      System.arraycopy(encryptedData, 0, encryptedWithIv, GCM_IV_LENGTH, encryptedData.length);

      // Return Base64 encoded result
      return Base64.getEncoder().encodeToString(encryptedWithIv);
    } catch (Exception e) {
      log.error("Failed to encrypt fingerprint data", e);
      throw new RuntimeException("Fingerprint encryption failed", e);
    }
  }

  /** Decrypt fingerprint data from storage. Uses AES-GCM for authenticated decryption. */
  public static String decryptFingerprint(String encryptedFingerprint) {
    if (encryptedFingerprint == null || encryptedFingerprint.isEmpty()) {
      return null;
    }

    try {
      // Decode Base64
      byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedFingerprint);

      // Extract IV and encrypted data
      byte[] iv = new byte[GCM_IV_LENGTH];
      byte[] encryptedData = new byte[encryptedWithIv.length - GCM_IV_LENGTH];
      System.arraycopy(encryptedWithIv, 0, iv, 0, GCM_IV_LENGTH);
      System.arraycopy(encryptedWithIv, GCM_IV_LENGTH, encryptedData, 0, encryptedData.length);

      // Create secret key from configured key
      SecretKey secretKey =
          new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);

      // Initialize cipher
      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
      cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

      // Decrypt data
      byte[] decryptedData = cipher.doFinal(encryptedData);

      return new String(decryptedData, StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.error("Failed to decrypt fingerprint data", e);
      throw new RuntimeException("Fingerprint decryption failed", e);
    }
  }
}
