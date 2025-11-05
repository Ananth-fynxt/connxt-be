package nexxus.pspgroup.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import nexxus.psp.dto.IdNameDto;
import nexxus.shared.constants.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PspGroupDto {
  private String id;

  private Integer version;

  @NotBlank(message = "Brand ID is required")
  private String brandId;

  @NotBlank(message = "Environment ID is required")
  private String environmentId;

  @NotBlank(message = "PSP group name is required")
  private String name;

  @NotBlank(message = "Flow Action ID is required")
  private String flowActionId;

  private String flowActionName;

  @NotBlank(message = "Currency is required")
  private String currency;

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
