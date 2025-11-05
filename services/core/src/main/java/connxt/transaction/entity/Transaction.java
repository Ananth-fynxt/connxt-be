package connxt.transaction.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

  @Column(name = "request_id")
  private String requestId;

  @Column(name = "flow_action_id")
  private String flowActionId;

  @Column(name = "flow_target_id")
  private String flowTargetId;

  @Column(name = "psp_id")
  private String pspId;

  @Column(name = "psp_txn_id")
  private String pspTxnId;

  @Column(name = "wallet_id")
  private String walletId;

  @Column(name = "external_request_id")
  private String externalRequestId;

  @Column(name = "customer_id")
  private String customerId;

  @Column(name = "customer_tag")
  private String customerTag;

  @Column(name = "customer_account_type")
  private String customerAccountType;

  @Column(name = "wallet_currency")
  private String walletCurrency;

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

  @Column(name = "txn_currency")
  private String txnCurrency;

  @Column(name = "txn_fee")
  private BigDecimal txnFee;

  @Column(name = "txn_amount")
  private BigDecimal txnAmount;

  @Column(name = "bo_approval_status")
  private String boApprovalStatus;

  @Column(name = "bo_approved_by")
  private String boApprovedBy;

  @Column(name = "bo_approval_date")
  private LocalDateTime boApprovalDate;

  @Column(name = "remarks")
  private String remarks;

  @Column(name = "received_amount")
  private BigDecimal receivedAmount;

  @Column(name = "received_currency")
  private String receivedCurrency;

  @Column(name = "inserted_by_ip_address")
  private String insertedByIpAddress;

  @Column(name = "updated_by_ip_address")
  private String updatedByIpAddress;

  @Type(JsonBinaryType.class)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "execute_payload", columnDefinition = "jsonb")
  private JsonNode executePayload;
}
