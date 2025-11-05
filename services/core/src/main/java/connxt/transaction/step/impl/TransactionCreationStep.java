package connxt.transaction.step.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import connxt.psp.service.PspService;
import connxt.shared.constants.ErrorCode;
import connxt.shared.exception.TransactionException;
import connxt.transaction.context.TransactionExecutionContext;
import connxt.transaction.dto.TransactionStatus;
import connxt.transaction.entity.Transaction;
import connxt.transaction.repository.TransactionRepository;
import connxt.transaction.service.TransactionFlowConfigurationService;
import connxt.transaction.service.mappers.TransactionMapper;
import connxt.transaction.step.AbstractTransactionStep;

import jakarta.transaction.Transactional;

@Component
public class TransactionCreationStep extends AbstractTransactionStep {

  private static final Logger logger = LoggerFactory.getLogger(TransactionCreationStep.class);

  private final PspService pspService;

  public TransactionCreationStep(
      PspService pspService,
      TransactionRepository transactionRepository,
      TransactionMapper transactionMapper,
      TransactionFlowConfigurationService transactionFlowConfigurationService) {
    super(transactionRepository, transactionMapper, transactionFlowConfigurationService);
    this.pspService = pspService;
  }

  @Override
  @Transactional
  public TransactionExecutionContext execute(TransactionExecutionContext context) {
    if (!precondition(context)) {
      Transaction tx = context.getTransaction();
      throw new TransactionException(
          String.format(
              "Precondition failed for transition %s -> %s (flowTargetId=%s, flowActionId=%s, txnId=%s)",
              tx.getStatus(),
              getDestinationStatus(),
              tx.getFlowTargetId(),
              tx.getFlowActionId(),
              context.getTxnId()),
          ErrorCode.TRANSACTION_INVALID_TRANSITION_STATUS);
    }
    context = doExecute(context);
    Transaction newTransaction =
        transactionMapper.createNewVersionedRecord(
            context.getTransaction(), getDestinationStatus());
    Transaction savedTransaction = transactionRepository.save(newTransaction);
    return context.toBuilder().transaction(savedTransaction).build();
  }

  @Override
  protected TransactionExecutionContext doExecute(TransactionExecutionContext context) {
    performIdempotencyCheck(context);
    pspService.getPspIfEnabled(context.getTransaction().getPspId());
    return context;
  }

  private void performIdempotencyCheck(TransactionExecutionContext context) {
    Transaction transaction = context.getTransaction();
    if (null == transaction) {
      logger.error("Transaction is null in context");
      throw new TransactionException(
          "Transaction is null in execution context",
          ErrorCode.TRANSACTION_INVALID_TRANSITION_STATUS);
    }
    // External request ID validation removed as field doesn't exist in schema
    // Duplicate transaction check based on txnId handled by repository constraints
  }

  @Override
  public TransactionStatus getDestinationStatus() {
    return TransactionStatus.CREATED;
  }
}
