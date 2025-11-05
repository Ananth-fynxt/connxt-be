package nexxus.riskrule.entity;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import nexxus.shared.constants.*;
import nexxus.shared.db.AuditingEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "risk_rule")
@Builder
public class RiskRule extends AuditingEntity {

  @EmbeddedId private EmbeddableRiskRuleId riskRuleId;

  @Column(name = "name")
  private String name;

  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(name = "type", columnDefinition = "risk_type")
  private RiskType type;

  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(name = "action", columnDefinition = "risk_action")
  private RiskAction action;

  @Column(name = "currency")
  private String currency;

  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(name = "duration", columnDefinition = "risk_duration")
  private RiskDuration duration;

  @Column(name = "max_amount")
  private BigDecimal maxAmount;

  @Column(name = "brand_id")
  private String brandId;

  @Column(name = "environment_id")
  private String environmentId;

  @Column(name = "flow_action_id")
  private String flowActionId;

  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(name = "criteria_type", columnDefinition = "risk_customer_criteria_type")
  private RiskCustomerCriteriaType criteriaType;

  @Column(name = "criteria_value")
  private List<String> criteriaValue;

  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  @Column(name = "status", columnDefinition = "status")
  private Status status;

  @OneToMany(mappedBy = "riskRule", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<RiskRulePsp> riskRulePsps = new java.util.ArrayList<>();
}
