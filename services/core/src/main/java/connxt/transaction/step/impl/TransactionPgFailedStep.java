package connxt.transaction.step.impl;

import org.springframework.stereotype.Component;

import connxt.denovm.dto.DenoVMResult;
import connxt.transaction.context.TransactionExecutionContext;
import connxt.transaction.dto.TransactionStatus;
import connxt.transaction.repository.TransactionRepository;
import connxt.transaction.service.TransactionFlowConfigurationService;
import connxt.transaction.service.mappers.TransactionMapper;
import connxt.transaction.step.AbstractTransactionStep;

@Component
public class TransactionPgFailedStep extends AbstractTransactionStep {

  public TransactionPgFailedStep(
      TransactionRepository transactionRepository,
      TransactionMapper transactionMapper,
      TransactionFlowConfigurationService transactionFlowConfigurationService) {
    super(transactionRepository, transactionMapper, transactionFlowConfigurationService);
  }

  @Override
  protected boolean customPrecondition(TransactionExecutionContext context) {
    // Custom business logic: check for successful PG webhook data
    DenoVMResult pgWebhookData = (DenoVMResult) context.getCustomData().get("pgWebhookData");
    return pgWebhookData != null && !pgWebhookData.isSuccess();
  }

  @Override
  protected TransactionExecutionContext doExecute(TransactionExecutionContext context) {
    return context;
  }

  @Override
  public TransactionStatus getDestinationStatus() {
    return TransactionStatus.PG_FAILED;
  }
}
