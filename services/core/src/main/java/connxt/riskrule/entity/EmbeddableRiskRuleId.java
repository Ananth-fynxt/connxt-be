package connxt.riskrule.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddableRiskRuleId implements Serializable {

  @Column(name = "id")
  @RiskRuleId
  private String id;

  @Column(name = "version")
  private Integer version;
}
