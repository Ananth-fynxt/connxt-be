package connxt.conversionrate.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import connxt.shared.constants.Status;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversionRateDto {
  private String id;

  private Integer version;

  @NotBlank(message = "Brand ID is required")
  private String brandId;

  @NotBlank(message = "Environment ID is required")
  private String environmentId;

  @Builder.Default private Status status = Status.ENABLED;

  @NotBlank(message = "Source currency is required")
  private String sourceCurrency;

  @NotBlank(message = "Target currency is required")
  private String targetCurrency;

  @DecimalMin(value = "0.0", inclusive = false, message = "Value must be greater than 0")
  private BigDecimal value;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;
}
