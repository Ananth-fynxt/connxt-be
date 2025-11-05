package connxt.transaction.step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import connxt.shared.constants.ErrorCode;
import connxt.shared.exception.TransactionException;
import connxt.transaction.context.TransactionExecutionContext;
import connxt.transaction.dto.TransactionStatus;
import connxt.transaction.entity.Transaction;
import connxt.transaction.repository.TransactionRepository;
import connxt.transaction.service.TransactionFlowConfigurationService;
import connxt.transaction.service.mappers.TransactionMapper;

public abstract class AbstractTransactionStep implements TransactionStep {

  private static final Logger logger = LoggerFactory.getLogger(AbstractTransactionStep.class);

  protected final TransactionRepository transactionRepository;
  protected final TransactionMapper transactionMapper;
  protected final TransactionFlowConfigurationService transactionFlowConfigurationService;

  protected AbstractTransactionStep(
      TransactionRepository transactionRepository,
      TransactionMapper transactionMapper,
      TransactionFlowConfigurationService transactionFlowConfigurationService) {

    this.transactionRepository = transactionRepository;
    this.transactionMapper = transactionMapper;
    this.transactionFlowConfigurationService = transactionFlowConfigurationService;

    // All dependencies injected successfully
    logger.debug("AbstractTransactionStep created with all required dependencies");
  }

  @Override
  public boolean precondition(TransactionExecutionContext context) {
    boolean statusTransitionAllowed = isStatusTransitionAllowed(context);
    boolean customLogicPassed = customPrecondition(context);
    return statusTransitionAllowed && customLogicPassed;
  }

  @Override
  @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
  public TransactionExecutionContext execute(TransactionExecutionContext context) {
    // Pessimistically lock the latest transaction record
    Transaction latestTransaction = getLatestTransactionForUpdate(context);
    context.setTransaction(latestTransaction);
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
    TransactionExecutionContext updatedContext = doExecute(context);
    return createAndSaveNewVersion(updatedContext);
  }

  @Override
  public abstract TransactionStatus getDestinationStatus();

  /**
   * Creates a new versioned transaction record with incremented version and updated status. The
   * pessimistic lock ensures we have the latest version, so we can safely increment.
   */
  protected TransactionExecutionContext createAndSaveNewVersion(
      TransactionExecutionContext context) {
    Transaction currentTransaction = context.getTransaction();
    Transaction newTransaction =
        transactionMapper.createNewVersionedRecord(currentTransaction, getDestinationStatus());
    Transaction savedTransaction = transactionRepository.save(newTransaction);
    return context.toBuilder().transaction(savedTransaction).build();
  }

  protected abstract TransactionExecutionContext doExecute(TransactionExecutionContext context);

  protected boolean customPrecondition(TransactionExecutionContext context) {
    return true;
  }

  private boolean isStatusTransitionAllowed(TransactionExecutionContext context) {
    Transaction transaction = context.getTransaction();
    String flowTargetId = transaction.getFlowTargetId();
    String flowActionId = transaction.getFlowActionId();
    TransactionStatus currentStatus = transaction.getStatus();
    TransactionStatus destinationStatus = getDestinationStatus();

    if (flowTargetId == null || flowActionId == null) {
      logger.warn("Missing flowTargetId or flowActionId for transaction {}", context.getTxnId());
      return false;
    }

    try {
      return transactionFlowConfigurationService.isValidTransition(
          flowTargetId, flowActionId, currentStatus, destinationStatus);
    } catch (Exception e) {
      logger.error("Failed to check status transition for transaction {}", context.getTxnId(), e);
      return false;
    }
  }

  /**
   * Pessimistically locks and retrieves the latest transaction record for update. This ensures no
   * other process can modify the record while we're working with it.
   */
  private Transaction getLatestTransactionForUpdate(TransactionExecutionContext context) {
    return transactionRepository.findLatestByTxnIdForUpdate(context.getTxnId());
  }
}
