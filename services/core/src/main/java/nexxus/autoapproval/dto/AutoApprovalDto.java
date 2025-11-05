package nexxus.autoapproval.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import nexxus.psp.dto.IdNameDto;
import nexxus.shared.constants.Status;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoApprovalDto {
  private String id;

  private Integer version;

  @NotBlank(message = "Auto approval name is required")
  private String name;

  @NotBlank(message = "Currency is required")
  private String currency;

  @NotEmpty(message = "At least one country is required")
  private List<String> countries;

  @NotBlank(message = "Brand ID is required")
  private String brandId;

  @NotBlank(message = "Environment ID is required")
  private String environmentId;

  @NotBlank(message = "Flow Action ID is required")
  private String flowActionId;

  private String flowActionName;

  @NotNull(message = "Maximum amount is required")
  @DecimalMin(value = "0.01", message = "Maximum amount must be greater than 0")
  private BigDecimal maxAmount;

  @Builder.Default private Status status = Status.ENABLED;

  @NotEmpty(message = "At least one PSP is required")
  private List<IdNameDto> psps;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;
}
