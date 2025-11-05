package nexxus.transaction.step.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import nexxus.autoapproval.service.AutoApprovalService;
import nexxus.transaction.context.TransactionExecutionContext;
import nexxus.transaction.dto.TransactionStatus;
import nexxus.transaction.entity.Transaction;
import nexxus.transaction.repository.TransactionRepository;
import nexxus.transaction.service.TransactionFlowConfigurationService;
import nexxus.transaction.service.mappers.TransactionMapper;
import nexxus.transaction.step.AbstractTransactionStep;

@Component
public class TransactionAutoApprovalStep extends AbstractTransactionStep {

  private final AutoApprovalService autoApprovalService;

  public TransactionAutoApprovalStep(
      AutoApprovalService autoApprovalService,
      TransactionRepository transactionRepository,
      TransactionMapper transactionMapper,
      TransactionFlowConfigurationService transactionFlowConfigurationService) {
    super(transactionRepository, transactionMapper, transactionFlowConfigurationService);
    this.autoApprovalService = autoApprovalService;
  }

  @Override
  protected boolean customPrecondition(TransactionExecutionContext context) {
    // Custom business logic: amount check
    return isAmountWithinAutoApprovalLimit(context.getTransaction());
  }

  private boolean isAmountWithinAutoApprovalLimit(Transaction transaction) {
    BigDecimal maxAmountForAutoApproval =
        autoApprovalService.getMaxAmountForFlowAction(transaction.getFlowActionId());
    return transaction.getTxnAmount().compareTo(maxAmountForAutoApproval) < 0;
  }

  @Override
  protected TransactionExecutionContext doExecute(TransactionExecutionContext context) {
    Transaction transaction = context.getTransaction();
    transaction.setBoApprovedBy("System");
    transaction.setBoApprovalDate(LocalDateTime.now());
    return context;
  }

  @Override
  public TransactionStatus getDestinationStatus() {
    return TransactionStatus.AUTO_APPROVED;
  }
}
