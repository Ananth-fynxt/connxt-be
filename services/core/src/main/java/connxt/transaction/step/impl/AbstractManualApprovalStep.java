package connxt.transaction.step.impl;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import connxt.transaction.context.TransactionExecutionContext;
import connxt.transaction.dto.ApprovalDecision;
import connxt.transaction.repository.TransactionRepository;
import connxt.transaction.service.TransactionFlowConfigurationService;
import connxt.transaction.service.mappers.TransactionMapper;
import connxt.transaction.step.AbstractTransactionStep;

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
    // Approval status fields don't exist in schema - store approval info in customData instead
    String approvedBy = (String) context.getCustomData().get("approvedBy");
    context.getCustomData().put("approvalStatus", getApprovalStatus().toString());
    context.getCustomData().put("approvalDate", LocalDateTime.now());
    if (approvedBy != null) {
      context.getCustomData().put("approvedBy", approvedBy);
    }
    return context;
  }

  protected boolean isDecisionMatch(String decision) {
    return getApprovalStatus().toString().equalsIgnoreCase(decision);
  }

  protected abstract ApprovalDecision getApprovalStatus();
}
