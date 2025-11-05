package connxt.session.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FingerprintDto {

  /** Client's IP address (better set server-side for integrity). */
  private String ipAddress;

  /** Browser User-Agent string */
  @NotBlank(message = "User agent is required")
  private String userAgent;

  /** OS platform (e.g., "MacIntel", "Win32", "Linux x86_64") */
  @NotBlank(message = "Platform is required")
  private String platform;

  /** Browser language (e.g., "en-US") */
  @NotBlank(message = "Language is required")
  private String language;

  /** Timezone identifier (e.g., "Asia/Kolkata") */
  @NotBlank(message = "Timezone is required")
  private String timezone;

  /** Number of logical CPU cores */
  @Positive(message = "Hardware concurrency must be positive")
  private Integer hardwareConcurrency;

  /** Approx device memory in GB (if available from browser) */
  @Positive(message = "Device memory must be positive")
  private Integer deviceMemory;

  /** Stable device ID (generated & stored on client for persistence) */
  @NotBlank(message = "Device ID is required")
  private String deviceId;

  /** Any extensible metadata (future-proofing) */
  private Object extra;
}
