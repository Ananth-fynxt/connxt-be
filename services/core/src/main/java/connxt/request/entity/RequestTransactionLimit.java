package connxt.request.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "request_transaction_limits")
@IdClass(RequestTransactionLimitId.class)
public class RequestTransactionLimit {

  @Id
  @Column(name = "request_id")
  private String requestId;

  @Id
  @Column(name = "transaction_limit_id")
  private String transactionLimitId;

  @Id
  @Column(name = "transaction_limit_version")
  private Integer transactionLimitVersion;
}
