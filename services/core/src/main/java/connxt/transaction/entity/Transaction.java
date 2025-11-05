package connxt.transaction.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import connxt.shared.db.AuditingEntity;
import connxt.shared.db.PostgreSQLEnumType;
import connxt.transaction.dto.TransactionStatus;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
@EqualsAndHashCode(callSuper = true)
public class Transaction extends AuditingEntity {

  @EmbeddedId private EmbeddableTransactionId id;

  @Column(name = "brand_id")
  private String brandId;

  @Column(name = "environment_id")
  private String environmentId;

  @Column(name = "flow_action_id")
  private String flowActionId;

  @Column(name = "flow_target_id")
  private String flowTargetId;

  @Column(name = "psp_id")
  private String pspId;

  @Column(name = "psp_txn_id")
  private String pspTxnId;

  @Column(name = "transaction_type")
  private String transactionType;

  @Type(
      value = PostgreSQLEnumType.class,
      parameters =
          @org.hibernate.annotations.Parameter(
              name = "enumClass",
              value = "connxt.transaction.dto.TransactionStatus"))
  @Enumerated(EnumType.STRING)
  @Column(name = "status", columnDefinition = "transaction_status")
  private TransactionStatus status;

  @Type(JsonBinaryType.class)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "execute_payload", columnDefinition = "jsonb")
  private JsonNode executePayload;
}
