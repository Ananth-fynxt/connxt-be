package nexxus.transaction.orchestrator;

import nexxus.transaction.context.TransactionExecutionContext;
import nexxus.transaction.dto.TransactionDto;
import nexxus.transaction.dto.TransactionStatus;

public interface TransactionOrchestrator {

  TransactionExecutionContext createTransaction(TransactionDto transactionDto);

  TransactionExecutionContext executeNextStep(TransactionExecutionContext context);

  TransactionExecutionContext transitionToStatus(
      TransactionExecutionContext context, TransactionStatus targetStatus);
}
