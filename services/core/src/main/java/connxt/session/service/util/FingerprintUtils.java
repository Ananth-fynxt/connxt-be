package connxt.session.service.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import connxt.session.dto.FingerprintDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FingerprintUtils {

  private static final ObjectMapper OBJECT_MAPPER =
      new ObjectMapper().configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

  /**
   * Generate a canonical hash of the fingerprint for consistent comparison. This method ensures the
   * same fingerprint always produces the same hash.
   */
  public static String generateCanonicalHash(FingerprintDto fingerprint) {
    if (fingerprint == null) {
      return null;
    }

    try {
      // Create a canonical JSON representation
      String canonicalJson = OBJECT_MAPPER.writeValueAsString(fingerprint);

      // Generate SHA-256 hash
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(canonicalJson.getBytes(StandardCharsets.UTF_8));

      // Return Base64 URL-safe encoded hash
      return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    } catch (Exception e) {
      log.error("Failed to generate canonical hash for fingerprint", e);
      throw new RuntimeException("Failed to generate fingerprint hash", e);
    }
  }

  /**
   * Generate a canonical string representation of the fingerprint for HMAC binding. This creates a
   * deterministic string that can be used for token binding.
   */
  public static String generateCanonicalString(FingerprintDto fingerprint) {
    if (fingerprint == null) {
      return "";
    }

    try {
      // Create a canonical JSON representation
      return OBJECT_MAPPER.writeValueAsString(fingerprint);
    } catch (JsonProcessingException e) {
      log.error("Failed to generate canonical string for fingerprint", e);
      throw new RuntimeException("Failed to generate fingerprint canonical string", e);
    }
  }

  /** Validate that a fingerprint contains the minimum required fields for security. */
  public static boolean isValidFingerprint(FingerprintDto fingerprint) {
    if (fingerprint == null) {
      return false;
    }

    // At minimum, we need userAgent and deviceId for strong binding
    return Objects.nonNull(fingerprint.getUserAgent())
        && !fingerprint.getUserAgent().trim().isEmpty()
        && Objects.nonNull(fingerprint.getDeviceId())
        && !fingerprint.getDeviceId().trim().isEmpty();
  }

  /**
   * Calculate a similarity score between two fingerprints (0.0 = identical, 1.0 = completely
   * different). This can be used for anomaly detection.
   */
  public static double calculateSimilarityScore(
      FingerprintDto fingerprint1, FingerprintDto fingerprint2) {
    if (fingerprint1 == null || fingerprint2 == null) {
      return 1.0; // Completely different if either is null
    }

    int totalFields = 0;
    int matchingFields = 0;

    // Compare each field with weighted importance
    if (compareField(fingerprint1.getUserAgent(), fingerprint2.getUserAgent())) {
      matchingFields++;
    }
    totalFields++;

    if (compareField(fingerprint1.getPlatform(), fingerprint2.getPlatform())) {
      matchingFields++;
    }
    totalFields++;

    if (compareField(fingerprint1.getLanguage(), fingerprint2.getLanguage())) {
      matchingFields++;
    }
    totalFields++;

    if (compareField(fingerprint1.getTimezone(), fingerprint2.getTimezone())) {
      matchingFields++;
    }
    totalFields++;

    if (compareField(
        fingerprint1.getHardwareConcurrency(), fingerprint2.getHardwareConcurrency())) {
      matchingFields++;
    }
    totalFields++;

    if (compareField(fingerprint1.getDeviceMemory(), fingerprint2.getDeviceMemory())) {
      matchingFields++;
    }
    totalFields++;

    if (compareField(fingerprint1.getDeviceId(), fingerprint2.getDeviceId())) {
      matchingFields++;
    }
    totalFields++;

    // Calculate similarity (0.0 = identical, 1.0 = completely different)
    return 1.0 - ((double) matchingFields / totalFields);
  }

  /**
   * Check if two fingerprints are similar enough to allow session operations. Uses configurable
   * threshold to handle minor changes (browser updates, network changes).
   *
   * <p>Critical fields (userAgent, deviceId) must match exactly for security.
   */
  public static boolean isFingerprintSimilar(
      FingerprintDto fingerprint1, FingerprintDto fingerprint2, double threshold) {
    if (fingerprint1 == null || fingerprint2 == null) {
      return false;
    }

    // Critical fields must match exactly for security
    if (!compareField(fingerprint1.getUserAgent(), fingerprint2.getUserAgent())) {
      log.warn(
          "Fingerprint validation failed: UserAgent mismatch - stored: '{}', provided: '{}'",
          fingerprint2.getUserAgent(),
          fingerprint1.getUserAgent());
      return false; // UserAgent must match exactly
    }

    if (!compareField(fingerprint1.getDeviceId(), fingerprint2.getDeviceId())) {
      log.warn(
          "Fingerprint validation failed: DeviceId mismatch - stored: '{}', provided: '{}'",
          fingerprint2.getDeviceId(),
          fingerprint1.getDeviceId());
      return false; // DeviceId must match exactly
    }

    // For other fields, use similarity threshold
    double similarityScore = calculateSimilarityScore(fingerprint1, fingerprint2);
    boolean isSimilar = similarityScore <= threshold;

    if (!isSimilar) {
      log.warn(
          "Fingerprint validation failed: Similarity score {} exceeds threshold {}",
          similarityScore,
          threshold);
    }

    return isSimilar; // Lower score = more similar
  }

  public static boolean isFingerprintSimilar(
      FingerprintDto fingerprint1, FingerprintDto fingerprint2) {
    return isFingerprintSimilar(fingerprint1, fingerprint2, 0.1);
  }

  /**
   * Detect IP address anomalies between fingerprints. Returns true if IP change is suspicious
   * (different country/region).
   */
  public static boolean detectIpAnomaly(FingerprintDto fingerprint1, FingerprintDto fingerprint2) {
    if (fingerprint1 == null || fingerprint2 == null) {
      return true; // Consider null fingerprints as anomaly
    }

    String ip1 = fingerprint1.getIpAddress();
    String ip2 = fingerprint2.getIpAddress();

    if (ip1 == null || ip2 == null) {
      return false; // No IP to compare
    }

    // Simple IP comparison - in production, you might want to use GeoIP lookup
    // to detect if IPs are from different countries/regions
    return !ip1.equals(ip2);
  }

  private static boolean compareField(Object field1, Object field2) {
    if (field1 == null && field2 == null) {
      return true;
    }
    if (field1 == null || field2 == null) {
      return false;
    }
    return field1.equals(field2);
  }
}
