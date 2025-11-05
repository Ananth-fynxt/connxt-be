package connxt.transactionlimit.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLimitPspActionDto {

  @NotBlank(message = "Flow Action ID is required")
  private String flowActionId;

  private String flowActionName;

  @NotNull(message = "Minimum amount is required")
  @DecimalMin(value = "0.00", message = "Minimum amount must be greater than or equal to 0")
  private BigDecimal minAmount;

  @NotNull(message = "Maximum amount is required")
  @DecimalMin(value = "0.01", message = "Maximum amount must be greater than 0")
  private BigDecimal maxAmount;
}
