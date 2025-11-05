package nexxus.conversionrate.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversionRateRawDataDto {
  private String id;

  private Integer version;

  @NotBlank(message = "Source currency is required")
  private String sourceCurrency;

  @NotBlank(message = "Target currency is required")
  private String targetCurrency;

  @NotNull(message = "Time range is required")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDateTime timeRange;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
  private BigDecimal amount;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;
}
