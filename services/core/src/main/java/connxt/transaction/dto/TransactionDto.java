package connxt.transaction.dto;

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
  private String flowActionId;
  private String flowTargetId;
  private String pspId;
  private String pspTxnId;
  private String transactionType;
  private TransactionStatus status;
  private Map<String, Object> executePayload;
  private Map<String, Object> customData;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String createdBy;
  private String updatedBy;
}
