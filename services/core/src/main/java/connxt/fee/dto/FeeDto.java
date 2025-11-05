package connxt.fee.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import connxt.fee.dto.validation.ValidFeeComponents;
import connxt.psp.dto.IdNameDto;
import connxt.shared.constants.ChargeFeeType;
import connxt.shared.constants.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeDto {
  private String id;

  private Integer version;

  @NotBlank(message = "Fee name is required")
  private String name;

  @NotBlank(message = "Currency is required")
  private String currency;

  @NotNull(message = "Charge fee type is required")
  private ChargeFeeType chargeFeeType;

  @NotBlank(message = "Brand ID is required")
  private String brandId;

  @NotBlank(message = "Environment ID is required")
  private String environmentId;

  @NotBlank(message = "Flow Action ID is required")
  private String flowActionId;

  private String flowActionName;

  @Builder.Default private Status status = Status.ENABLED;

  @NotEmpty(message = "At least one component is required")
  @ValidFeeComponents
  private List<FeeComponentDto> components;

  @NotEmpty(message = "At least one country is required")
  private List<String> countries;

  @NotEmpty(message = "At least one PSP is required")
  private List<IdNameDto> psps;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;
}
