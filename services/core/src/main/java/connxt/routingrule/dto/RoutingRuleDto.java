package connxt.routingrule.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;

import connxt.routingrule.dto.validation.PspSelectionModeValidation;
import connxt.shared.constants.PspSelectionMode;
import connxt.shared.constants.RoutingDuration;
import connxt.shared.constants.RoutingType;
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
@PspSelectionModeValidation
public class RoutingRuleDto {

  private String id;

  private Integer version;

  @NotBlank(message = "Name is required")
  private String name;

  @NotBlank(message = "Brand ID is required")
  private String brandId;

  @NotBlank(message = "Environment ID is required")
  private String environmentId;

  @NotNull(message = "PSP selection mode is required")
  private PspSelectionMode pspSelectionMode;

  private RoutingType routingType;

  private RoutingDuration duration;

  private JsonNode conditionJson;

  @Builder.Default private Status status = Status.ENABLED;

  @NotEmpty(message = "At least one PSP is required")
  private List<RoutingRulePspDto> psps;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime updatedAt;

  private String createdBy;

  private String updatedBy;
}
