package connxt.transaction.step;

import connxt.transaction.context.TransactionExecutionContext;
import connxt.transaction.dto.TransactionStatus;

public interface TransactionStep {
  boolean precondition(TransactionExecutionContext context);

  TransactionExecutionContext execute(TransactionExecutionContext context);

  TransactionStatus getDestinationStatus();
}
