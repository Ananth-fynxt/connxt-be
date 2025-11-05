package nexxus.transaction.step;

import nexxus.transaction.context.TransactionExecutionContext;
import nexxus.transaction.dto.TransactionStatus;

public interface TransactionStep {
  boolean precondition(TransactionExecutionContext context);

  TransactionExecutionContext execute(TransactionExecutionContext context);

  TransactionStatus getDestinationStatus();
}
