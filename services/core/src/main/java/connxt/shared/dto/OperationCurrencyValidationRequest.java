package connxt.shared.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request DTO for validating operation currencies against flow target. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationCurrencyValidationRequest {

  @NotBlank(message = "Flow target ID is required")
  private String flowTargetId;

  @NotEmpty(message = "Operations list cannot be empty")
  @Valid
  private List<PspOperation> operations;

  /** Represents a PSP operation with its currencies */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PspOperation {

    @NotBlank(message = "Flow action ID is required")
    private String flowActionId;

    @Valid private List<CurrencyInfo> currencies;
  }

  /** Represents currency information */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CurrencyInfo {

    @NotBlank(message = "Currency code is required")
    private String currency;
  }
}
