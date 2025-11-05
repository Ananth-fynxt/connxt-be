package connxt.transaction.step.factory;

import java.util.EnumMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import connxt.transaction.dto.TransactionStatus;
import connxt.transaction.step.TransactionStep;

@Component
public class TransactionStepFactory {

  private static final Logger logger = LoggerFactory.getLogger(TransactionStepFactory.class);

  private final EnumMap<TransactionStatus, TransactionStep> transactionStepMap;

  public TransactionStepFactory(List<TransactionStep> transactionSteps) {
    this.transactionStepMap = new EnumMap<>(TransactionStatus.class);
    for (TransactionStep transactionStep : transactionSteps) {
      transactionStepMap.put(transactionStep.getDestinationStatus(), transactionStep);
    }
    logger.info(
        "Initialized TransactionStepFactory with {} transaction steps: {}",
        transactionSteps.size(),
        transactionStepMap.keySet());
  }

  public TransactionStep getStepForStatus(TransactionStatus status) {
    TransactionStep step = transactionStepMap.get(status);
    if (step == null) {
      logger.warn(
          "No step found for status: {}. Available statuses: {}",
          status,
          transactionStepMap.keySet());
    }
    return step;
  }
}
