package connxt.request.entity;

import java.math.BigDecimal;

import connxt.shared.db.AuditingEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests")
@EqualsAndHashCode(callSuper = true)
public class Request extends AuditingEntity {

  @Id @RequestId private String id;

  @Column(name = "brand_id", nullable = false)
  private String brandId;

  @Column(name = "environment_id", nullable = false)
  private String environmentId;

  @Column(name = "customer_id", nullable = false)
  private String customerId;

  @Column(name = "wallet_id")
  private String walletId;

  @Column(name = "customer_tag")
  private String customerTag;

  @Column(name = "customer_account_type")
  private String customerAccountType;

  @Column(name = "flow_action_id", nullable = false)
  private String flowActionId;

  @Column(name = "amount")
  private BigDecimal amount;

  @Column(name = "currency")
  private String currency;

  @Column(name = "country")
  private String country;

  @Column(name = "routing_rule_id")
  private String routingRuleId;

  @Column(name = "routing_rule_version")
  private Integer routingRuleVersion;
}
