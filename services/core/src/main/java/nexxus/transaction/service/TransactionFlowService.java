package nexxus.transaction.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import nexxus.transaction.context.TransactionExecutionContext;
import nexxus.transaction.dto.TransactionApprovalRequest;
import nexxus.transaction.dto.TransactionDto;
import nexxus.transaction.dto.TransactionStatus;
import nexxus.transaction.entity.Transaction;
import nexxus.transaction.orchestrator.TransactionOrchestrator;
import nexxus.transaction.repository.TransactionRepository;
import nexxus.transaction.service.mappers.TransactionMapper;

@Service
public class TransactionFlowService {

  private static final Logger logger = LoggerFactory.getLogger(TransactionFlowService.class);

  @Autowired private TransactionOrchestrator orchestrator;
  @Autowired private TransactionMapper transactionMapper;
  @Autowired private TransactionRepository transactionRepository;

  public TransactionDto createTransaction(TransactionDto transactionDto) {
    TransactionExecutionContext resultContext = orchestrator.createTransaction(transactionDto);
    return transactionMapper.toDto(resultContext);
  }

  public TransactionDto moveToStatus(TransactionDto transactionDto, TransactionStatus status) {
    Transaction transaction = transactionMapper.toEntity(transactionDto);
    TransactionExecutionContext context =
        TransactionExecutionContext.builder()
            .transaction(transaction)
            .isFirstExecution(false)
            .build();
    TransactionExecutionContext resultContext = orchestrator.transitionToStatus(context, status);
    return transactionMapper.toDto(resultContext.getTransaction());
  }

  public TransactionDto processManualApproval(TransactionApprovalRequest approvalRequest) {
    logger.info(
        "Processing manual approval for transaction: {} with decision: {}",
        approvalRequest.getTxnId(),
        approvalRequest.getDecision());
    Transaction transaction = transactionRepository.findLatestByTxnId(approvalRequest.getTxnId());
    String approvedBy =
        SecurityContextHolder.getContext().getAuthentication() != null
            ? SecurityContextHolder.getContext().getAuthentication().getName()
            : "system";
    TransactionExecutionContext context =
        TransactionExecutionContext.builder()
            .transaction(transaction)
            .txnId(approvalRequest.getTxnId())
            .isFirstExecution(false)
            .build();

    context.getCustomData().put("decision", approvalRequest.getDecision().toString());
    context.getCustomData().put("approvedBy", approvedBy);

    TransactionExecutionContext resultContext = orchestrator.executeNextStep(context);
    logger.info(
        "Successfully processed approval for transaction: {} with decision: {} by user: {}",
        approvalRequest.getTxnId(),
        approvalRequest.getDecision(),
        approvedBy);
    return transactionMapper.toDto(resultContext);
  }
}
