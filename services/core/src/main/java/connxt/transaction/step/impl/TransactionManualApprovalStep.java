package connxt.transaction.step.impl;

import org.springframework.stereotype.Component;

import connxt.transaction.dto.ApprovalDecision;
import connxt.transaction.dto.TransactionStatus;
import connxt.transaction.repository.TransactionRepository;
import connxt.transaction.service.TransactionFlowConfigurationService;
import connxt.transaction.service.mappers.TransactionMapper;

@Component
public class TransactionManualApprovalStep extends AbstractManualApprovalStep {

  public TransactionManualApprovalStep(
      TransactionRepository transactionRepository,
      TransactionMapper transactionMapper,
      TransactionFlowConfigurationService transactionFlowConfigurationService) {
    super(transactionRepository, transactionMapper, transactionFlowConfigurationService);
  }

  @Override
  protected ApprovalDecision getApprovalStatus() {
    return ApprovalDecision.APPROVED;
  }

  @Override
  public TransactionStatus getDestinationStatus() {
    return TransactionStatus.APPROVED;
  }
}
