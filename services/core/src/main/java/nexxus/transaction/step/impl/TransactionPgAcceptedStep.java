package nexxus.transaction.step.impl;

import org.springframework.stereotype.Component;

import nexxus.denovm.dto.DenoVMResult;
import nexxus.transaction.context.TransactionExecutionContext;
import nexxus.transaction.dto.TransactionStatus;
import nexxus.transaction.repository.TransactionRepository;
import nexxus.transaction.service.TransactionFlowConfigurationService;
import nexxus.transaction.service.mappers.TransactionMapper;
import nexxus.transaction.step.AbstractTransactionStep;

@Component
public class TransactionPgAcceptedStep extends AbstractTransactionStep {

  public TransactionPgAcceptedStep(
      TransactionRepository transactionRepository,
      TransactionMapper transactionMapper,
      TransactionFlowConfigurationService transactionFlowConfigurationService) {
    super(transactionRepository, transactionMapper, transactionFlowConfigurationService);
  }

  @Override
  protected boolean customPrecondition(TransactionExecutionContext context) {
    // Custom business logic: check for successful PG redirect data
    if (context.getCustomData() != null && context.getCustomData().containsKey("pgRedirectData")) {
      DenoVMResult pgRedirectData = (DenoVMResult) context.getCustomData().get("pgRedirectData");
      return pgRedirectData != null && pgRedirectData.isSuccess();
    }
    return false;
  }

  @Override
  protected TransactionExecutionContext doExecute(TransactionExecutionContext context) {
    return context;
  }

  @Override
  public TransactionStatus getDestinationStatus() {
    return TransactionStatus.PG_ACCEPTED;
  }
}
