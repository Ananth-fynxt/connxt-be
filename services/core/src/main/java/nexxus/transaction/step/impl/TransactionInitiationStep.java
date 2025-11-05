package nexxus.transaction.step.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import nexxus.denovm.dto.DenoVMResult;
import nexxus.external.dto.VmExecutionDto;
import nexxus.external.service.VMExecuteService;
import nexxus.transaction.context.TransactionExecutionContext;
import nexxus.transaction.dto.TransactionStatus;
import nexxus.transaction.repository.TransactionRepository;
import nexxus.transaction.service.TransactionFlowConfigurationService;
import nexxus.transaction.service.mappers.TransactionMapper;
import nexxus.transaction.step.AbstractTransactionStep;

@Component
public class TransactionInitiationStep extends AbstractTransactionStep {

  private static final Logger log = LoggerFactory.getLogger(TransactionInitiationStep.class);

  private final VMExecuteService vmExecuteService;
  private final ObjectMapper objectMapper;

  public TransactionInitiationStep(
      VMExecuteService vmExecuteService,
      ObjectMapper objectMapper,
      TransactionRepository transactionRepository,
      TransactionMapper transactionMapper,
      TransactionFlowConfigurationService transactionFlowConfigurationService) {
    super(transactionRepository, transactionMapper, transactionFlowConfigurationService);
    this.vmExecuteService = vmExecuteService;
    this.objectMapper = objectMapper;
  }

  @Override
  protected TransactionExecutionContext doExecute(TransactionExecutionContext context) {
    VmExecutionDto vmExecutionDto =
        VmExecutionDto.builder()
            .pspId(context.getTransaction().getPspId())
            .amount(context.getTransaction().getTxnAmount().longValue())
            .currency(context.getTransaction().getTxnCurrency())
            .brandId(context.getTransaction().getBrandId())
            .environmentId(context.getTransaction().getEnvironmentId())
            .step("initiate")
            .flowActionId(context.getTransaction().getFlowActionId())
            .walletId(context.getTransaction().getWalletId())
            .transactionId(context.getTransaction().getId().getTxnId())
            .executePayload(convertJsonNodeToMap(context.getTransaction().getExecutePayload()))
            .build();

    log.info("Executing VM execution for vmExecutionDto: {}", vmExecutionDto);

    DenoVMResult response = vmExecuteService.executeVmRequest(vmExecutionDto);
    context.getCustomData().put("vmExecutionResponse", response);
    return context;
  }

  @Override
  public TransactionStatus getDestinationStatus() {
    return TransactionStatus.INITIATED;
  }

  private java.util.Map<String, Object> convertJsonNodeToMap(
      com.fasterxml.jackson.databind.JsonNode jsonNode) {
    if (jsonNode == null) {
      return null;
    }
    try {
      return objectMapper.convertValue(
          jsonNode,
          new com.fasterxml.jackson.core.type.TypeReference<java.util.Map<String, Object>>() {});
    } catch (Exception e) {
      log.warn("Failed to convert JsonNode to Map, returning null", e);
      return null;
    }
  }
}
