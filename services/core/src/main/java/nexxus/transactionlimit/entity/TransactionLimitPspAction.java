package nexxus.transactionlimit.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_limit_psps_actions")
@IdClass(TransactionLimitPspActionId.class)
public class TransactionLimitPspAction {

  @Id
  @Column(name = "transaction_limit_id")
  private String transactionLimitId;

  @Id
  @Column(name = "transaction_limit_version")
  private Integer transactionLimitVersion;

  @Id
  @Column(name = "flow_action_id")
  private String flowActionId;

  @Column(name = "min_amount")
  private BigDecimal minAmount;

  @Column(name = "max_amount")
  private BigDecimal maxAmount;
}
