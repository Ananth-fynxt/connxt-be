package connxt.external.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import connxt.denovm.dto.DenoVMResult;
import connxt.environment.dto.EnvironmentDto;
import connxt.environment.service.EnvironmentService;
import connxt.external.dto.VmExecutionDto;
import connxt.external.service.ExternalService;
import connxt.external.service.VMExecuteService;
import connxt.shared.constants.ErrorCode;
import connxt.shared.constants.WebhookStatusType;
import connxt.transaction.context.TransactionExecutionContext;
import connxt.transaction.dto.TransactionDto;
import connxt.transaction.orchestrator.impl.TransactionOrchestratorImpl;
import connxt.transaction.service.TransactionService;
import connxt.transaction.service.mappers.TransactionMapper;
import connxt.webhook.service.WebhookExecutionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalServiceImpl implements ExternalService {

  private final TransactionService transactionService;
  private final EnvironmentService environmentService;
  private final VMExecuteService vmExecuteService;
  private final TransactionOrchestratorImpl transactionOrchestratorImpl;
  private final TransactionMapper transactionMapper;
  private final WebhookExecutionService webhookExecutionService;

  @Override
  public Object read(Map<String, Object> externalDto) {
    String step = (String) externalDto.get("step");
    String token = (String) externalDto.get("token");
    String tnxId = (String) externalDto.get("tnxId");

    EnvironmentDto environmentDto = environmentService.readByToken(token);
    if (environmentDto == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, ErrorCode.ENVIRONMENT_NOT_FOUND.getCode());
    }

    TransactionDto transactionDto = transactionService.read(tnxId);
    if (transactionDto == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, ErrorCode.TRANSACTION_NOT_FOUND.getCode());
    }

    if (!environmentDto.getId().equals(transactionDto.getEnvironmentId())
        || !environmentDto.getBrandId().equals(transactionDto.getBrandId())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, ErrorCode.TRANSACTION_VALIDATION_FAILED.getCode());
    }

    try {

      VmExecutionDto vmExecutionDto =
          VmExecutionDto.builder()
              .pspId(transactionDto.getPspId())
              .flowActionId(transactionDto.getFlowActionId())
              .transactionId(tnxId)
              .step(step)
              .executePayload(externalDto)
              .environmentId(transactionDto.getEnvironmentId())
              .brandId(transactionDto.getBrandId())
              .token(token)
              .build();

      DenoVMResult vmResponse = vmExecuteService.executeVmRequest(vmExecutionDto);

      TransactionExecutionContext context =
          TransactionExecutionContext.builder()
              .transaction(transactionMapper.toEntity(transactionDto))
              .build();

      String pgData = step.equals("redirect") ? "pgRedirectData" : "pgWebhookData";
      context.getCustomData().put(pgData, vmResponse);

      transactionOrchestratorImpl.executeNextStep(context);
      sendWebhookAfterTransaction(transactionDto, vmResponse, step);

      return vmResponse;
    } catch (Exception e) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process inbound request: " + e.getMessage());
    }
  }

  private void sendWebhookAfterTransaction(
      TransactionDto transactionDto, DenoVMResult vmResponse, String step) {
    try {
      String brandId = transactionDto.getBrandId();
      String environmentId = transactionDto.getEnvironmentId();
      String transactionId = transactionDto.getTxnId();

      WebhookStatusType webhookStatusType = determineWebhookStatusType(step, vmResponse);

      Map<String, Object> webhookPayload = createWebhookPayload(transactionDto, vmResponse, step);

      webhookExecutionService.sendWebhook(
          brandId, environmentId, webhookStatusType, webhookPayload, transactionId);

      log.info(
          "Webhook sent successfully for transaction: {}, brand: {}, environment: {}, statusType: {}",
          transactionId,
          brandId,
          environmentId,
          webhookStatusType);

    } catch (Exception e) {
      log.error(
          "Failed to send webhook for transaction: {}, error: {}",
          transactionDto.getTxnId(),
          e.getMessage(),
          e);
    }
  }

  private WebhookStatusType determineWebhookStatusType(String step, DenoVMResult vmResponse) {
    if ("redirect".equals(step)) {
      if (null != vmResponse.getData()) {
        return WebhookStatusType.SUCCESS;
      } else {
        return WebhookStatusType.FAILURE;
      }
    } else if ("webhook".equals(step)) {
      return WebhookStatusType.NOTIFICATION;
    } else {
      return WebhookStatusType.NOTIFICATION;
    }
  }

  private Map<String, Object> createWebhookPayload(
      TransactionDto transactionDto, DenoVMResult vmResponse, String step) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("transactionId", transactionDto.getTxnId());
    payload.put("brandId", transactionDto.getBrandId());
    payload.put("environmentId", transactionDto.getEnvironmentId());
    payload.put("pspId", transactionDto.getPspId());
    payload.put("flowActionId", transactionDto.getFlowActionId());
    payload.put("externalRequestId", transactionDto.getExternalRequestId());
    payload.put("customerId", transactionDto.getCustomerId());
    payload.put("customerTag", transactionDto.getCustomerTag());
    payload.put("customerAccountType", transactionDto.getCustomerAccountType());
    payload.put("remarks", transactionDto.getRemarks());
    payload.put("step", step);
    payload.put("timestamp", System.currentTimeMillis());
    payload.put("response", vmResponse.getData() != null ? vmResponse.getData() : null);
    return payload;
  }

  @Override
  public String extractRedirectUrl(Object result, String token, String tnxId, String step) {
    try {
      // Handle DenoVMResult specifically
      if (result instanceof DenoVMResult) {
        DenoVMResult denoResult = (DenoVMResult) result;

        // Extract from data field only
        if (denoResult.getData() != null && denoResult.getData() instanceof Map) {
          @SuppressWarnings("unchecked")
          Map<String, Object> dataMap = (Map<String, Object>) denoResult.getData();

          // Look for "url" field only
          Object urlValue = dataMap.get("url");
          if (urlValue instanceof String) {
            String url = (String) urlValue;
            if (isValidUrl(url)) {
              return url;
            }
          }
        }
      }

      return null;
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public String getEnvironmentOrigin(String tnxId) {
    TransactionDto transactionDto = transactionService.read(tnxId);

    EnvironmentDto environmentDto = environmentService.read(transactionDto.getEnvironmentId());

    if (environmentDto == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Environment not found");
    }

    return environmentDto.getOrigin();
  }

  private boolean isValidUrl(String url) {
    if (url == null || url.trim().isEmpty()) {
      return false;
    }

    String trimmedUrl = url.trim();

    return trimmedUrl.startsWith("http://")
        || trimmedUrl.startsWith("https://")
        || trimmedUrl.startsWith("/");
  }
}
