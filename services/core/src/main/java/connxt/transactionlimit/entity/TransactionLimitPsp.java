package connxt.transactionlimit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_limit_psps")
@IdClass(TransactionLimitPspId.class)
public class TransactionLimitPsp {

  @Id
  @Column(name = "transaction_limit_id")
  private String transactionLimitId;

  @Id
  @Column(name = "transaction_limit_version")
  private Integer transactionLimitVersion;

  @Id
  @Column(name = "psp_id")
  private String pspId;
}
