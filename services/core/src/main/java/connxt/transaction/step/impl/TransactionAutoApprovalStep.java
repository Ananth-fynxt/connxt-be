package connxt.transaction.step.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import connxt.autoapproval.service.AutoApprovalService;
import connxt.transaction.context.TransactionExecutionContext;
import connxt.transaction.dto.TransactionStatus;
import connxt.transaction.entity.Transaction;
import connxt.transaction.repository.TransactionRepository;
import connxt.transaction.service.TransactionFlowConfigurationService;
import connxt.transaction.service.mappers.TransactionMapper;
import connxt.transaction.step.AbstractTransactionStep;

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
