package connxt.psp.dto;

import java.math.BigDecimal;
import java.util.List;

import connxt.shared.constants.Status;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePspDto {

  private String name;
  private Status status;
  private String brandId;
  private String environmentId;
  private String flowTargetId;
  private String description;
  private String logo;
  private String credential;
  private Integer timeout;
  private Boolean blockVpnAccess;
  private Boolean blockDataCenterAccess;
  private Boolean failureRate;
  private Integer failureRateThreshold;
  private Integer failureRateDurationMinutes;
  private List<String> ipAddress;

  @Valid private List<MaintenanceWindowDto> maintenanceWindow;

  @Valid private List<PspOperationDto> operations;

  private String updatedBy;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class MaintenanceWindowDto {
    private String flowActionId;
    private String startAt;
    private String endAt;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PspOperationDto {
    private String flowActionId;
    private String flowDefinitionId;
    private Status status;
    @Valid private List<String> currencies;
    @Valid private List<String> countries;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CurrencyDto {
    private String currency;
    private BigDecimal minValue;
    private BigDecimal maxValue;
  }
}
