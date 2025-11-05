package nexxus.riskrule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "risk_rule_psps")
@Builder
@IdClass(RiskRulePspId.class)
public class RiskRulePsp {

  @Id
  @Column(name = "risk_rule_id")
  private String riskRuleId;

  @Id
  @Column(name = "risk_rule_version")
  private Integer riskRuleVersion;

  @Id
  @Column(name = "psp_id")
  private String pspId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumns({
    @JoinColumn(
        name = "risk_rule_id",
        referencedColumnName = "id",
        insertable = false,
        updatable = false),
    @JoinColumn(
        name = "risk_rule_version",
        referencedColumnName = "version",
        insertable = false,
        updatable = false)
  })
  private RiskRule riskRule;
}
