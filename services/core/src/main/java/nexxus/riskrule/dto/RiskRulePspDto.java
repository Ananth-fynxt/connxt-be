package nexxus.riskrule.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskRulePspDto {
  @NotBlank(message = "PSP ID is required")
  private String id;

  private String name;
}
