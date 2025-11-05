package connxt.shared.util;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for encrypting and decrypting sensitive data like credentials Uses AES-GCM
 * encryption for secure storage
 */
@Slf4j
@Component
public class CryptoUtil {

  private final ObjectMapper objectMapper;
  private final String secretKey;
  private static final String ALGORITHM = "AES/GCM/NoPadding";
  private static final int GCM_IV_LENGTH = 12;
  private static final int GCM_TAG_LENGTH = 16;

  public CryptoUtil(
      @Value("${connxt.crypto.secret-key:default-secret-key-change-in-production}")
          String secretKey) {
    this.secretKey = normalizeSecretKey(secretKey);
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Normalizes the secret key to ensure it's the correct length for AES Uses SHA-256 hash to create
   * a 32-byte key from any input
   *
   * @param secretKey The original secret key
   * @return Normalized 32-byte key for AES-256
   */
  private String normalizeSecretKey(String secretKey) {
    try {
      java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(secretKey.getBytes(StandardCharsets.UTF_8));
      return java.util.Base64.getEncoder().encodeToString(hash);
    } catch (Exception e) {
      log.error("Error normalizing secret key: {}", e.getMessage());
      // Fallback to a default key if normalization fails
      return "default-secret-key-change-in-production-32-chars";
    }
  }

  /**
   * Encrypts a credential object by encrypting each value individually
   *
   * @param credential The credential object to encrypt
   * @return Encrypted credential object
   * @throws Exception if encryption fails
   */
  public Map<String, String> encryptCredential(Map<String, String> credential) throws Exception {
    if (credential == null || credential.isEmpty()) {
      return credential;
    }

    Map<String, String> encrypted = new HashMap<>();

    for (Map.Entry<String, String> entry : credential.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();

      if (value != null && !value.trim().isEmpty()) {
        String encryptedValue = encrypt(value);
        encrypted.put(key, encryptedValue);
      } else {
        encrypted.put(key, value); // Keep null/empty values as is
      }
    }

    return encrypted;
  }

  /**
   * Decrypts a credential object by decrypting each value individually
   *
   * @param encryptedCredential The encrypted credential object to decrypt
   * @return Decrypted credential object
   * @throws Exception if decryption fails
   */
  public Map<String, String> decryptCredential(Map<String, String> encryptedCredential)
      throws Exception {
    if (encryptedCredential == null || encryptedCredential.isEmpty()) {
      return encryptedCredential;
    }

    Map<String, String> decrypted = new HashMap<>();

    for (Map.Entry<String, String> entry : encryptedCredential.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();

      if (value != null && !value.trim().isEmpty() && isEncrypted(value)) {
        String decryptedValue = decrypt(value);
        decrypted.put(key, decryptedValue);
      } else {
        decrypted.put(key, value); // Keep non-encrypted values as is
      }
    }

    return decrypted;
  }

  /**
   * Encrypts a single string value
   *
   * @param plaintext The text to encrypt
   * @return Base64 encoded encrypted string
   * @throws Exception if encryption fails
   */
  public String encrypt(String plaintext) throws Exception {
    if (plaintext == null || plaintext.trim().isEmpty()) {
      return plaintext;
    }

    try {
      // Decode the normalized secret key from Base64
      byte[] keyBytes = Base64.getDecoder().decode(secretKey);

      // Generate a random IV
      byte[] iv = new byte[GCM_IV_LENGTH];
      SecureRandom random = new SecureRandom();
      random.nextBytes(iv);

      // Create cipher
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
      GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
      cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

      // Encrypt
      byte[] encrypted = cipher.doFinal(plaintext.getBytes());

      // Combine IV and encrypted data
      byte[] combined = new byte[iv.length + encrypted.length];
      System.arraycopy(iv, 0, combined, 0, iv.length);
      System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

      return Base64.getEncoder().encodeToString(combined);
    } catch (Exception e) {
      log.error("Error encrypting value: {}", e.getMessage());
      throw new Exception("Encryption failed: " + e.getMessage());
    }
  }

  /**
   * Decrypts a single string value
   *
   * @param encryptedText The Base64 encoded encrypted text to decrypt
   * @return Decrypted string
   * @throws Exception if decryption fails
   */
  public String decrypt(String encryptedText) throws Exception {
    if (encryptedText == null || encryptedText.trim().isEmpty()) {
      return encryptedText;
    }

    try {
      // Decode the normalized secret key from Base64
      byte[] keyBytes = Base64.getDecoder().decode(secretKey);

      // Decode from Base64
      byte[] combined = Base64.getDecoder().decode(encryptedText);

      // Extract IV and encrypted data
      byte[] iv = new byte[GCM_IV_LENGTH];
      byte[] encrypted = new byte[combined.length - GCM_IV_LENGTH];
      System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
      System.arraycopy(combined, GCM_IV_LENGTH, encrypted, 0, encrypted.length);

      // Create cipher
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
      GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
      cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

      // Decrypt
      byte[] decrypted = cipher.doFinal(encrypted);
      return new String(decrypted);
    } catch (Exception e) {
      log.error("Error decrypting value: {}", e.getMessage());
      throw new Exception("Decryption failed: " + e.getMessage());
    }
  }

  /**
   * Checks if a string appears to be encrypted (Base64 encoded with proper length)
   *
   * @param value The string to check
   * @return true if the string appears to be encrypted
   */
  private boolean isEncrypted(String value) {
    try {
      // Check if it's Base64 encoded
      byte[] decoded = Base64.getDecoder().decode(value);
      // Check if it has the minimum length for IV + some encrypted data
      return decoded.length >= GCM_IV_LENGTH + 1;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Converts a JSON string credential to a Map for encryption
   *
   * @param credentialJson JSON string representation of credentials
   * @return Map of credential key-value pairs
   * @throws Exception if JSON parsing fails
   */
  @SuppressWarnings("unchecked")
  public Map<String, String> parseCredentialJson(String credentialJson) throws Exception {
    if (credentialJson == null || credentialJson.trim().isEmpty()) {
      return new HashMap<>();
    }

    try {
      return objectMapper.readValue(credentialJson, Map.class);
    } catch (Exception e) {
      log.error("Error parsing credential JSON: {}", e.getMessage());
      throw new Exception("Invalid credential JSON format: " + e.getMessage());
    }
  }

  /**
   * Converts a Map of credentials to JSON string
   *
   * @param credentialMap Map of credential key-value pairs
   * @return JSON string representation
   * @throws Exception if JSON serialization fails
   */
  public String credentialMapToJson(Map<String, String> credentialMap) throws Exception {
    if (credentialMap == null || credentialMap.isEmpty()) {
      return "{}";
    }

    try {
      return objectMapper.writeValueAsString(credentialMap);
    } catch (Exception e) {
      log.error("Error serializing credential map to JSON: {}", e.getMessage());
      throw new Exception("Error serializing credentials: " + e.getMessage());
    }
  }

  /**
   * Encrypts a JsonNode credential by encrypting each field individually while preserving the JSON
   * structure
   *
   * @param credentialJsonNode The JsonNode credential to encrypt
   * @return Encrypted JsonNode credential with same structure
   * @throws Exception if encryption fails
   */
  public JsonNode encryptCredentialJsonNode(JsonNode credentialJsonNode) throws Exception {
    if (credentialJsonNode == null || credentialJsonNode.isNull() || credentialJsonNode.isEmpty()) {
      return credentialJsonNode;
    }

    try {
      // Convert JsonNode to Map for encryption
      @SuppressWarnings("unchecked")
      Map<String, String> credentialMap = objectMapper.convertValue(credentialJsonNode, Map.class);

      // Encrypt the credential map
      Map<String, String> encryptedMap = encryptCredential(credentialMap);

      // Convert back to JsonNode
      return objectMapper.valueToTree(encryptedMap);
    } catch (Exception e) {
      log.error("Error encrypting JsonNode credential: {}", e.getMessage());
      throw new Exception("Encryption failed for JsonNode: " + e.getMessage());
    }
  }

  /**
   * Decrypts a JsonNode credential by decrypting each field individually while preserving the JSON
   * structure
   *
   * @param encryptedCredentialJsonNode The encrypted JsonNode credential to decrypt
   * @return Decrypted JsonNode credential with same structure
   * @throws Exception if decryption fails
   */
  public JsonNode decryptCredentialJsonNode(JsonNode encryptedCredentialJsonNode) throws Exception {
    if (encryptedCredentialJsonNode == null
        || encryptedCredentialJsonNode.isNull()
        || encryptedCredentialJsonNode.isEmpty()) {
      return encryptedCredentialJsonNode;
    }

    try {
      // Convert JsonNode to Map for decryption
      @SuppressWarnings("unchecked")
      Map<String, String> encryptedMap =
          objectMapper.convertValue(encryptedCredentialJsonNode, Map.class);

      // Decrypt the credential map
      Map<String, String> decryptedMap = decryptCredential(encryptedMap);

      // Convert back to JsonNode
      return objectMapper.valueToTree(decryptedMap);
    } catch (Exception e) {
      log.error("Error decrypting JsonNode credential: {}", e.getMessage());
      throw new Exception("Decryption failed for JsonNode: " + e.getMessage());
    }
  }

  /**
   * Checks if a JsonNode credential appears to be encrypted by checking if any field contains
   * encrypted data
   *
   * @param credentialJsonNode The JsonNode to check
   * @return true if the JsonNode appears to contain encrypted data
   */
  public boolean isCredentialJsonNodeEncrypted(JsonNode credentialJsonNode) {
    if (credentialJsonNode == null || credentialJsonNode.isNull() || credentialJsonNode.isEmpty()) {
      return false;
    }

    try {
      @SuppressWarnings("unchecked")
      Map<String, String> credentialMap = objectMapper.convertValue(credentialJsonNode, Map.class);

      // Check if any field contains encrypted data
      for (Map.Entry<String, String> entry : credentialMap.entrySet()) {
        String value = entry.getValue();
        if (value != null && !value.trim().isEmpty() && isEncrypted(value)) {
          return true;
        }
      }
      return false;
    } catch (Exception e) {
      log.debug("Error checking if JsonNode credential is encrypted: {}", e.getMessage());
      return false;
    }
  }
}
