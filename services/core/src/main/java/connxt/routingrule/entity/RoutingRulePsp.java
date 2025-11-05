package connxt.routingrule.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "routing_rule_psps")
@IdClass(RoutingRulePspId.class)
public class RoutingRulePsp {

  @Id
  @Column(name = "routing_rule_id")
  private String routingRuleId;

  @Id
  @Column(name = "routing_rule_version")
  private Integer routingRuleVersion;

  @Id
  @Column(name = "psp_id")
  private String pspId;

  @Column(name = "psp_order")
  private Integer pspOrder;

  @Column(name = "psp_value")
  private Integer pspValue;
}
