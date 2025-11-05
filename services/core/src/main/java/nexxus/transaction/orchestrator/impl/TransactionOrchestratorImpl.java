package nexxus.transaction.orchestrator.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import nexxus.shared.constants.ErrorCode;
import nexxus.shared.exception.TransactionException;
import nexxus.transaction.context.TransactionExecutionContext;
import nexxus.transaction.dto.TransactionDto;
import nexxus.transaction.dto.TransactionStatus;
import nexxus.transaction.entity.Transaction;
import nexxus.transaction.orchestrator.TransactionOrchestrator;
import nexxus.transaction.service.TransactionFlowConfigurationService;
import nexxus.transaction.service.mappers.TransactionMapper;
import nexxus.transaction.step.TransactionStep;
import nexxus.transaction.step.factory.TransactionStepFactory;

/**
 * Central orchestrator that manages transaction step transitions and execution. Validates
 * transitions based on the factory configuration and executes appropriate steps.
 */
@Component
public class TransactionOrchestratorImpl implements TransactionOrchestrator {

  private static final Logger logger = LoggerFactory.getLogger(TransactionOrchestratorImpl.class);

  @Autowired private TransactionStepFactory stepFactory;

  @Autowired private TransactionFlowConfigurationService flowConfigurationService;

  @Autowired private TransactionMapper transactionMapper;

  public TransactionExecutionContext createTransaction(TransactionDto transactionDto) {
    logger.debug(
        "Starting transaction flow for flowTargetId: {}, flowActionId: {}",
        transactionDto.getFlowTargetId(),
        transactionDto.getFlowActionId());
    Transaction transaction = transactionMapper.toEntity(transactionDto);
    transaction.setStatus(TransactionStatus.NEW);
    TransactionExecutionContext context =
        TransactionExecutionContext.builder()
            .transaction(transaction)
            .isFirstExecution(true)
            .build();
    return executeNextStep(context);
  }

  public TransactionExecutionContext executeNextStep(TransactionExecutionContext context) {
    TransactionStep nextStep = determineNextStep(context);
    return executeStep(context, nextStep);
  }

  public TransactionExecutionContext transitionToStatus(
      TransactionExecutionContext context, TransactionStatus targetStatus) {
    logTransitionAttempt(context.getTransaction(), targetStatus);
    verifyTransition(context.getTransaction(), targetStatus);
    TransactionStep targetStep = getTransactionStep(targetStatus);
    return executeStep(context, targetStep);
  }

  private TransactionStep getTransactionStep(TransactionStatus targetStatus) {
    logger.debug("Requesting step for status: {}", targetStatus);
    TransactionStep targetStep = stepFactory.getStepForStatus(targetStatus);
    if (targetStep == null) {
      logger.error("No transaction step found for status: {}", targetStatus);
      throw new TransactionException(
          "No transaction step found for status: " + targetStatus,
          ErrorCode.TRANSACTION_PROCESSING_ERROR);
    }
    return targetStep;
  }

  private TransactionStep determineNextStep(TransactionExecutionContext context) {
    List<TransactionStep> validSteps = getValidNextSteps(context);
    if (validSteps.isEmpty()) {
      logNoValidSteps(context.getTransaction());
      throw new TransactionException(
          "No valid transaction steps found", ErrorCode.TRANSACTION_NO_VALID_STEPS_FOUND);
    }
    if (validSteps.size() > 1) {
      logMultipleValidSteps(context.getTransaction(), validSteps);
      throw new TransactionException(
          "Multiple valid transaction steps found", ErrorCode.TRANSACTION_MULTIPLE_STEPS_FOUND);
    }
    return validSteps.getFirst();
  }

  private List<TransactionStep> getValidNextSteps(TransactionExecutionContext context) {
    List<TransactionStatus> possibleNextStatuses =
        getPossibleNextStatuses(context.getTransaction());
    return findValidSteps(possibleNextStatuses, context);
  }

  private List<TransactionStatus> getPossibleNextStatuses(Transaction transaction) {
    logger.debug(
        "Getting next statuses for flowTargetId: {}, flowActionId: {}, currentStatus: {}",
        transaction.getFlowTargetId(),
        transaction.getFlowActionId(),
        transaction.getStatus());
    List<TransactionStatus> nextStatuses =
        flowConfigurationService.getNextStatuses(
            transaction.getFlowTargetId(), transaction.getFlowActionId(), transaction.getStatus());
    logger.debug("Found {} possible next statuses: {}", nextStatuses.size(), nextStatuses);
    return nextStatuses;
  }

  private List<TransactionStep> findValidSteps(
      List<TransactionStatus> possibleStatuses, TransactionExecutionContext context) {
    List<TransactionStep> validSteps = new ArrayList<>();
    logger.debug("Finding valid steps from possible statuses: {}", possibleStatuses);
    for (TransactionStatus status : possibleStatuses) {
      TransactionStep step = stepFactory.getStepForStatus(status);
      if (step != null && step.precondition(context)) {
        validSteps.add(step);
      }
    }
    return validSteps;
  }

  private TransactionExecutionContext executeStep(
      TransactionExecutionContext context, TransactionStep step) {
    logStepExecution(context.getTransaction(), step);
    try {
      context = step.execute(context);
      logStepSuccess(context.getTransaction(), step);
      context.setFirstExecution(false);
      return executeNextStep(context);
    } catch (Exception e) {
      logStepFailure(context.getTransaction(), step, e);
      if (context.isFirstExecution()) {
        throw e;
      }
      return context;
    }
  }

  private void verifyTransition(Transaction transaction, TransactionStatus targetStatus) {
    TransactionStatus currentStatus = transaction.getStatus();
    if (!flowConfigurationService.isValidTransition(
        transaction.getFlowTargetId(),
        transaction.getFlowActionId(),
        currentStatus,
        targetStatus)) {
      throw new TransactionException(
          "Transaction transition not valid", ErrorCode.TRANSACTION_INVALID_STATUS);
    }
  }

  private void logTransitionAttempt(Transaction transaction, TransactionStatus targetStatus) {
    logger.info(
        "Attempting to transition transaction {} from {} to {}",
        transaction.getId().getTxnId(),
        transaction.getStatus(),
        targetStatus);
  }

  private void logNoValidSteps(Transaction transaction) {
    logger.warn(
        "No valid steps found for transaction {} from status: {}",
        transaction.getId().getTxnId(),
        transaction.getStatus());
  }

  private void logMultipleValidSteps(Transaction transaction, List<TransactionStep> validSteps) {
    logger.warn(
        "Multiple valid steps found for transaction {}: {}. Executing first one.",
        transaction.getId().getTxnId(),
        validSteps.stream().map(TransactionStep::getDestinationStatus).toList());
  }

  private void logStepExecution(Transaction transaction, TransactionStep step) {
    logger.info(
        "Executing step: {} for transaction {}",
        step.getDestinationStatus(),
        transaction.getId().getTxnId());
  }

  private void logStepSuccess(Transaction transaction, TransactionStep step) {
    logger.info(
        "Successfully executed step: {} for transaction {}",
        step.getDestinationStatus(),
        transaction.getId().getTxnId());
  }

  private void logStepFailure(Transaction transaction, TransactionStep step, Exception e) {
    logger.error(
        "Failed to execute step: {} for transaction {}",
        step.getDestinationStatus(),
        transaction.getId().getTxnId(),
        e);
  }
}
