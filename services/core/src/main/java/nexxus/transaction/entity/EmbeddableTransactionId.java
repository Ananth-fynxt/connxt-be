package nexxus.transaction.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class EmbeddableTransactionId implements Serializable {

  @TransactionId
  @Column(name = "txn_id")
  private String txnId;

  @Column(name = "version")
  private Integer version;
}
