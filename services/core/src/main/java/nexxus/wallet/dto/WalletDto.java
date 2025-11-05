package nexxus.wallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletDto {
  private String id;

  @NotBlank(message = "Brand ID is required")
  private String brandId;

  @NotBlank(message = "Environment ID is required")
  private String environmentId;

  @NotBlank(message = "Brand Customer ID is required")
  private String brandCustomerId;

  private String name;

  @NotBlank(message = "Currency is required")
  private String currency;

  @NotNull(message = "Balance is required")
  @Builder.Default
  private BigDecimal balance = BigDecimal.ZERO;

  @NotNull(message = "Available Balance is required")
  @Builder.Default
  private BigDecimal availableBalance = BigDecimal.ZERO;

  @NotNull(message = "Hold Balance is required")
  @Builder.Default
  private BigDecimal holdBalance = BigDecimal.ZERO;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;
}
