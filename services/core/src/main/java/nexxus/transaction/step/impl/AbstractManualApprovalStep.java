package nexxus.transaction.step.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import nexxus.transaction.context.TransactionExecutionContext;
import nexxus.transaction.dto.ApprovalDecision;
import nexxus.transaction.entity.Transaction;
import nexxus.transaction.repository.TransactionRepository;
import nexxus.transaction.service.TransactionFlowConfigurationService;
import nexxus.transaction.service.mappers.TransactionMapper;
import nexxus.transaction.step.AbstractTransactionStep;

/**
 * Abstract base class for manual approval steps using Template Method pattern. Provides common
 * functionality while allowing subclasses to define specific behavior.
 */
public abstract class AbstractManualApprovalStep extends AbstractTransactionStep {

  protected AbstractManualApprovalStep(
      TransactionRepository transactionRepository,
      TransactionMapper transactionMapper,
      TransactionFlowConfigurationService transactionFlowConfigurationService) {
    super(transactionRepository, transactionMapper, transactionFlowConfigurationService);
  }

  @Override
  protected boolean customPrecondition(TransactionExecutionContext context) {
    Map<String, Object> customData = context.getCustomData();
    if (CollectionUtils.isEmpty(customData)) {
      return false;
    }

    Object decisionObj = customData.get("decision");
    if (!(decisionObj instanceof String decision)) {
      return false;
    }

    return isDecisionMatch(decision);
  }

  @Override
  protected TransactionExecutionContext doExecute(TransactionExecutionContext context) {
    Transaction transaction = context.getTransaction();
    transaction.setBoApprovalDate(LocalDateTime.now());
    String approvedBy = (String) context.getCustomData().get("approvedBy");
    transaction.setBoApprovedBy(approvedBy);
    transaction.setBoApprovalStatus(getApprovalStatus().toString());
    return context;
  }

  protected boolean isDecisionMatch(String decision) {
    return getApprovalStatus().toString().equalsIgnoreCase(decision);
  }

  protected abstract ApprovalDecision getApprovalStatus();
}
