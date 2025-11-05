package connxt.external.dto;

import java.util.Map;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VmExecutionDto {
  private String pspId;
  private String token;
  private String flowTargetId;
  @Positive private Long amount;
  private String currency;
  private String brandId;
  private String environmentId;
  private String step;
  private String flowActionId;
  private String walletId;
  private String transactionId;
  private Map<String, Object> executePayload;
}
