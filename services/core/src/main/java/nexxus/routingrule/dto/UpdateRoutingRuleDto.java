package nexxus.routingrule.dto;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import nexxus.routingrule.dto.validation.PspSelectionModeValidation;
import nexxus.shared.constants.PspSelectionMode;
import nexxus.shared.constants.RoutingDuration;
import nexxus.shared.constants.RoutingType;
import nexxus.shared.constants.Status;

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
