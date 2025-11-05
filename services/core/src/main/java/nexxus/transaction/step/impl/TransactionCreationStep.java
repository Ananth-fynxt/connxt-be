package nexxus.transaction.step.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.networknt.schema.utils.StringUtils;

import nexxus.psp.service.PspService;
import nexxus.shared.constants.ErrorCode;
import nexxus.shared.exception.ErrorCategory;
import nexxus.shared.exception.TransactionException;
import nexxus.transaction.context.TransactionExecutionContext;
import nexxus.transaction.dto.TransactionStatus;
import nexxus.transaction.entity.Transaction;
import nexxus.transaction.repository.TransactionRepository;
import nexxus.transaction.service.TransactionFlowConfigurationService;
import nexxus.transaction.service.mappers.TransactionMapper;
import nexxus.transaction.step.AbstractTransactionStep;

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
    validateExternalRequestIdPresence(transaction);
    validateDuplicateTransaction(transaction);
  }

  private void validateExternalRequestIdPresence(Transaction transaction) {
    if (null == transaction) {
      logger.error("Transaction is null in context");
      throw new TransactionException(
          "Transaction is null in execution context",
          ErrorCode.TRANSACTION_INVALID_TRANSITION_STATUS);
    }

    if (StringUtils.isBlank(transaction.getExternalRequestId())) {
      throw new TransactionException(
          "Transaction external request Id is required",
          ErrorCode.TRANSACTION_REQUEST_ID_NOT_FOUND);
    }
  }

  private void validateDuplicateTransaction(Transaction transaction) {
    Transaction existingTransaction =
        transactionRepository.findLatestByExternalRequestId(transaction.getExternalRequestId());

    if (null == existingTransaction) {
      return;
    }

    logger.warn(
        "Duplicate transaction detected: Found existing transaction {} for external request ID {}.",
        existingTransaction.getId() != null ? existingTransaction.getId().getTxnId() : "NULL",
        transaction.getExternalRequestId());

    throw new TransactionException(
        String.format(
            "Transaction with external request ID '%s' already exists. Existing transaction ID: %s",
            transaction.getExternalRequestId(), existingTransaction.getId().getTxnId()),
        ErrorCode.TRANSACTION_DUPLICATE,
        ErrorCategory.DUPLICATE);
  }

  @Override
  public TransactionStatus getDestinationStatus() {
    return TransactionStatus.CREATED;
  }
}
