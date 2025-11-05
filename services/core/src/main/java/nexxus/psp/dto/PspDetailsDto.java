package nexxus.psp.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import nexxus.fee.dto.FeeDto;
import nexxus.riskrule.dto.RiskRuleDto;
import nexxus.shared.constants.Status;
import nexxus.transactionlimit.dto.TransactionLimitDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PspDetailsDto {

  private String id;
  private String name;
  private String description;
  private String logo;
  private String credential;
  private Integer timeout;
  private Boolean blockVpnAccess;
  private Boolean blockDataCenterAccess;
  private Boolean failureRate;
  private Float failureRateThreshold;
  private Integer failureRateDurationMinutes;
  private String brandId;
  private String environmentId;
  private String flowTargetId;
  private Status status;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;
  private String updatedBy;
  private List<String> ipAddress;
  private List<MaintenanceWindowDto> maintenanceWindow;
  private List<PspOperationDto> operations;
  private FlowTargetInfo flowTarget;
  private List<RiskRuleDto> riskRules;
  private List<FeeDto> feeRules;
  private List<TransactionLimitDto> transactionLimits;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class MaintenanceWindowDto {
    private String id;

    private String flowActionId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endAt;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PspOperationDto {
    private String flowActionId;
    private String flowDefinitionId;
    private Status status;
    private List<String> currencies;
    private List<String> countries;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SupportedActionInfo {
    private String flowActionId;
    private String flowDefinitionId;
    private String flowActionName;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FlowTargetInfo {
    private String id;
    private String credentialSchema;
    private String flowTypeId;
    private List<String> currencies;
    private List<String> countries;
    private List<String> paymentMethods;
    private List<SupportedActionInfo> supportedActions;
  }
}
