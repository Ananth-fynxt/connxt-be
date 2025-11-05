package connxt.transaction.orchestrator;

import connxt.transaction.context.TransactionExecutionContext;
import connxt.transaction.dto.TransactionDto;
import connxt.transaction.dto.TransactionStatus;

public interface TransactionOrchestrator {

  TransactionExecutionContext createTransaction(TransactionDto transactionDto);

  TransactionExecutionContext executeNextStep(TransactionExecutionContext context);

  TransactionExecutionContext transitionToStatus(
      TransactionExecutionContext context, TransactionStatus targetStatus);
}
