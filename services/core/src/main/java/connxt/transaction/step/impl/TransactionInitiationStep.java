package connxt.transaction.step.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import connxt.denovm.dto.DenoVMResult;
import connxt.external.dto.VmExecutionDto;
import connxt.external.service.VMExecuteService;
import connxt.transaction.context.TransactionExecutionContext;
import connxt.transaction.dto.TransactionStatus;
import connxt.transaction.repository.TransactionRepository;
import connxt.transaction.service.TransactionFlowConfigurationService;
import connxt.transaction.service.mappers.TransactionMapper;
import connxt.transaction.step.AbstractTransactionStep;

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
    // Extract amount and currency from executePayload if available
    Long amount = extractAmountFromPayload(context.getTransaction().getExecutePayload());
    String currency = extractCurrencyFromPayload(context.getTransaction().getExecutePayload());
    String walletId = extractWalletIdFromPayload(context.getTransaction().getExecutePayload());

    VmExecutionDto vmExecutionDto =
        VmExecutionDto.builder()
            .pspId(context.getTransaction().getPspId())
            .amount(amount)
            .currency(currency)
            .brandId(context.getTransaction().getBrandId())
            .environmentId(context.getTransaction().getEnvironmentId())
            .step("initiate")
            .flowActionId(context.getTransaction().getFlowActionId())
            .walletId(walletId)
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

  private Long extractAmountFromPayload(com.fasterxml.jackson.databind.JsonNode payload) {
    if (payload == null || !payload.has("order") || !payload.get("order").has("money")) {
      return null;
    }
    try {
      return payload.get("order").get("money").get("amount").asLong();
    } catch (Exception e) {
      log.warn("Failed to extract amount from payload", e);
      return null;
    }
  }

  private String extractCurrencyFromPayload(com.fasterxml.jackson.databind.JsonNode payload) {
    if (payload == null || !payload.has("order") || !payload.get("order").has("money")) {
      return null;
    }
    try {
      return payload.get("order").get("money").get("currency").asText();
    } catch (Exception e) {
      log.warn("Failed to extract currency from payload", e);
      return null;
    }
  }

  private String extractWalletIdFromPayload(com.fasterxml.jackson.databind.JsonNode payload) {
    if (payload == null) {
      return null;
    }
    try {
      if (payload.has("walletId")) {
        return payload.get("walletId").asText();
      }
      return null;
    } catch (Exception e) {
      log.warn("Failed to extract walletId from payload", e);
      return null;
    }
  }
}
