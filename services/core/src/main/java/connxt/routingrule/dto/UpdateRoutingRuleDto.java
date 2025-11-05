package connxt.routingrule.dto;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import connxt.routingrule.dto.validation.PspSelectionModeValidation;
import connxt.shared.constants.PspSelectionMode;
import connxt.shared.constants.RoutingDuration;
import connxt.shared.constants.RoutingType;
import connxt.shared.constants.Status;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PspSelectionModeValidation
public class UpdateRoutingRuleDto {

  private String name;

  private PspSelectionMode pspSelectionMode;

  private RoutingType routingType;

  private RoutingDuration duration;

  private JsonNode conditionJson;

  private Status status;

  @NotEmpty(message = "At least one PSP is required")
  private List<RoutingRulePspDto> psps;

  private String updatedBy;
}
