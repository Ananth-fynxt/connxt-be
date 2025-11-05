package nexxus.request.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestOutputDto {

  private String requestId;

  private List<PspInfo> psps;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PspInfo {
    private String id;
    private String name;
    private String description;
    private String logo;
    private String brandId;
    private String environmentId;
    private String flowActionId;
    private String flowDefintionId;
    private String currency;
    private String walletId;
    private BigDecimal originalAmount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal appliedFeeAmount;

    private BigDecimal totalAmount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal netAmountToUser;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal inclusiveFeeAmount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal exclusiveFeeAmount;

    private boolean isFeeApplied;

    private boolean isConversionApplied;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String conversionFromCurrency;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String conversionToCurrency;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal conversionExchangeRate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal conversionConvertedAmount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private FlowTargetData flowTarget;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FlowTargetData {
    private String flowTargetId;
    private String inputSchema;
  }
}
