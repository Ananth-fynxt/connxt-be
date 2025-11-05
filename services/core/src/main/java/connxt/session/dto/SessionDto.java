package connxt.session.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

import connxt.session.dto.validation.ValidFingerprint;
import connxt.session.dto.validation.ValidSessionConfig;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class SessionDto {

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CreateRequest {
    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotBlank(message = "Brand ID is required")
    private String brandId;

    @NotBlank(message = "Environment ID is required")
    private String environmentId;

    @Valid
    @ValidSessionConfig
    @NotNull(message = "Session config is required")
    private Config sessionConfig;

    @Valid private DeviceInfo deviceInfo;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RefreshRequest {
    @NotBlank(message = "Session token is required")
    private String sessionToken;

    @Valid
    @ValidFingerprint
    @NotNull(message = "Fingerprint is required for security")
    private FingerprintDto fingerprint;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ValidateRequest {
    @NotBlank(message = "Session token is required")
    private String sessionToken;

    @Valid
    @ValidFingerprint
    @NotNull(message = "Fingerprint is required for security")
    private FingerprintDto fingerprint;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RevokeRequest {
    @NotBlank(message = "Session ID is required")
    private String sessionId;

    private String revokedBy; // Optional - defaults to "system" if not provided
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CreateResponse {
    private String sessionToken;
    private String sessionId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant expiresAt;

    private Integer timeoutMinutes;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RefreshResponse {
    private String sessionToken;
    private String sessionId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant expiresAt;

    private Integer extensionCount;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ValidateResponse {
    private Boolean valid;
    private String sessionId;
    private Boolean revoked;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant expiresAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant lastAccessedAt;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Config {
    @Min(value = 1, message = "Timeout minutes must be at least 1")
    @Max(value = 1440, message = "Timeout minutes cannot exceed 1440 (24 hours)")
    private Integer timeoutMinutes;

    @NotNull(message = "Max extensions is required")
    @Min(value = 0, message = "Max extensions must be at least 0")
    @Max(value = 10, message = "Max extensions cannot exceed 10")
    private Integer maxExtensions;

    private Boolean autoExtend;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DeviceInfo {
    @Valid
    @ValidFingerprint
    @NotNull(message = "Fingerprint is required for security")
    private FingerprintDto fingerprint;
  }
}
