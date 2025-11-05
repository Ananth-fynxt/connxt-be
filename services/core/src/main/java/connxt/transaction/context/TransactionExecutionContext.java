package connxt.transaction.context;

import java.util.HashMap;
import java.util.Map;

import connxt.transaction.entity.Transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TransactionExecutionContext {

  private Transaction transaction;
  private String txnId;
  private boolean isFirstExecution;
  @Builder.Default private Map<String, Object> customData = new HashMap<>();

  public <T> T getCustomData(String key, Class<T> type) {
    Object value = customData.get(key);
    if (value != null && type.isAssignableFrom(value.getClass())) {
      @SuppressWarnings("unchecked")
      T result = (T) value;
      return result;
    }
    return null;
  }

  public <T> T getCustomData(String key, Class<T> type, T defaultValue) {
    T value = getCustomData(key, type);
    return value != null ? value : defaultValue;
  }

  /**
   * Gets the transaction ID from either the txnId field or from the transaction entity.
   *
   * @return the transaction ID, or null if neither is available
   */
  public String getTxnId() {
    if (txnId != null) {
      return txnId;
    }
    if (transaction != null && transaction.getId() != null) {
      return transaction.getId().getTxnId();
    }
    return null;
  }
}
