package connxt.transaction.step.impl;

import org.springframework.stereotype.Component;

import connxt.transaction.context.TransactionExecutionContext;
import connxt.transaction.dto.TransactionStatus;
import connxt.transaction.repository.TransactionRepository;
import connxt.transaction.service.TransactionFlowConfigurationService;
import connxt.transaction.service.mappers.TransactionMapper;
import connxt.transaction.step.AbstractTransactionStep;

@Component
public class TransactionSuccessStep extends AbstractTransactionStep {

  public TransactionSuccessStep(
      TransactionRepository transactionRepository,
      TransactionMapper transactionMapper,
      TransactionFlowConfigurationService transactionFlowConfigurationService) {
    super(transactionRepository, transactionMapper, transactionFlowConfigurationService);
  }

  @Override
  protected TransactionExecutionContext doExecute(TransactionExecutionContext context) {
    return context;
  }

  @Override
  public TransactionStatus getDestinationStatus() {
    return TransactionStatus.SUCCESS;
  }
}
