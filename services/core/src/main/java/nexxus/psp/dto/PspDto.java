package nexxus.psp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import nexxus.shared.constants.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PspDto {
  private String id;

  @NotBlank(message = "PSP name is required")
  private String name;

  private String description;

  private String logo;

  @NotBlank(message = "Credential is required")
  private String credential;

  @Positive(message = "Timeout must be greater than 0")
  @Builder.Default
  private Integer timeout = 300;

  @Builder.Default private boolean blockVpnAccess = false;

  @Builder.Default private Boolean blockDataCenterAccess = false;

  @Builder.Default private Boolean failureRate = false;

  @Positive(message = "Failure rate threshold must be greater than 0")
  private Integer failureRateThreshold;

  @Positive(message = "Failure rate duration minutes must be greater than 0")
  private Integer failureRateDurationMinutes;

  private List<String> ipAddress;

  @NotBlank(message = "Brand ID is required")
  private String brandId;

  @NotBlank(message = "Environment ID is required")
  private String environmentId;

  @NotBlank(message = "Flow target ID is required")
  private String flowTargetId;

  @Builder.Default private Status status = Status.ENABLED;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;

  private List<MaintenanceWindowDto> maintenanceWindow;

  private List<PspOperationDto> operations;

  private FlowTargetInfo flowTarget;

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class MaintenanceWindowDto {
    private String flowActionId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endAt;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CurrencyDto {
    private String flowActionId;

    private String currency;

    private BigDecimal minValue;

    private BigDecimal maxValue;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PspOperationDto {
    private String flowActionId;

    private String flowDefinitionId;

    private Status status;

    private List<CurrencyDto> currencies;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SupportedActionInfo {
    private String flowActionId;

    private String flowDefinitionId;

    private String flowActionName;
  }

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FlowTargetInfo {
    private String id;

    private String credentialSchema;

    private String flowTargetName;

    private String flowTypeId;

    private List<String> supportedCurrencies;

    private List<SupportedActionInfo> supportedActions;
  }
}
