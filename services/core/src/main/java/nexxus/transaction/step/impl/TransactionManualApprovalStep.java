package nexxus.transaction.step.impl;

import org.springframework.stereotype.Component;

import nexxus.transaction.dto.ApprovalDecision;
import nexxus.transaction.dto.TransactionStatus;
import nexxus.transaction.repository.TransactionRepository;
import nexxus.transaction.service.TransactionFlowConfigurationService;
import nexxus.transaction.service.mappers.TransactionMapper;

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
