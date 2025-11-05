package connxt.fee.dto;

import java.math.BigDecimal;

import connxt.shared.constants.FeeComponentType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeComponentDto {
  private String id;

  @NotNull(message = "Component type is required")
  private FeeComponentType type;

  @NotNull(message = "Component amount is required")
  @DecimalMin(value = "0.01", message = "Component amount must be greater than 0")
  private BigDecimal amount;

  @DecimalMin(value = "0", message = "Min value must be 0 or greater")
  private BigDecimal minValue;

  @DecimalMin(value = "0", message = "Max value must be 0 or greater")
  private BigDecimal maxValue;
}
