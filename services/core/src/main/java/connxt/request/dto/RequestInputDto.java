package connxt.request.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestInputDto {

  @NotBlank(message = "Brand ID is required")
  private String brandId;

  @NotBlank(message = "Environment ID is required")
  private String environmentId;

  @NotNull(message = "Amount is required")
  @Positive(message = "Amount must be positive")
  private BigDecimal amount;

  private String walletId;

  @NotBlank(message = "Currency is required")
  private String currency;

  @NotBlank(message = "Action ID is required")
  private String actionId;

  @NotBlank(message = "Country is required")
  private String country;

  @NotBlank(message = "Customer ID is required")
  private String customerId;

  @NotBlank(message = "Customer Tag is required")
  private String customerTag;

  @NotBlank(message = "Customer Account Type is required")
  private String customerAccountType;

  private String routingRuleId;

  private String clientIpAddress;
}
