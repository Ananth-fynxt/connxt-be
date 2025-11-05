package nexxus.routingrule.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutingRulePspDto {

  @NotBlank(message = "PSP ID is required")
  private String pspId;

  private String pspName;

  private Integer pspOrder;

  private Integer pspValue;
}
