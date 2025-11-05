package nexxus.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Transaction entity. Used in TransactionExecutionContext to separate
 * concerns between business logic and persistence.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
  private String brandId;
  private String environmentId;
  private String txnId;
  private int version;
  private String requestId;
  private String flowActionId;
  private String flowTargetId;
  private String flowDefinitionId;
  private String pspId;
  private String pspTxnId;
  private String walletId;
  private String externalRequestId;
  private String customerId;
  private String customerTag;
  private String customerAccountType;

  private String walletCurrency;
  private String transactionType;
  private TransactionStatus status;
  private String txnCurrency;
  private BigDecimal txnFee;
  private BigDecimal txnAmount;
  private String boApprovedBy;
  private LocalDateTime boApprovalDate;
  private String remarks;
  private BigDecimal receivedAmount;
  private String receivedCurrency;

  private Map<String, Object> executePayload;
  private Map<String, Object> customData;

  private String insertedByIpAddress;
  private String updatedByIpAddress;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String createdBy;
  private String updatedBy;
}
