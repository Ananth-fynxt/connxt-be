package nexxus.external.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import nexxus.denovm.dto.DenoVMRequest;
import nexxus.denovm.dto.DenoVMResult;
import nexxus.denovm.service.DenoVMService;
import nexxus.environment.dto.EnvironmentDto;
import nexxus.environment.service.EnvironmentService;
import nexxus.external.dto.VmExecutionDto;
import nexxus.external.service.VMExecuteService;
import nexxus.flowaction.dto.FlowActionDto;
import nexxus.flowaction.service.FlowActionService;
import nexxus.flowdefinition.dto.FlowDefinitionDto;
import nexxus.flowdefinition.service.FlowDefinitionService;
import nexxus.psp.entity.Psp;
import nexxus.psp.entity.PspOperation;
import nexxus.psp.service.PspOperationsService;
import nexxus.psp.service.PspService;
import nexxus.shared.constants.ErrorCode;
import nexxus.shared.util.CredentialSanitizer;
import nexxus.shared.util.CryptoUtil;
import nexxus.shared.validators.JsonSchemaAndPayloadValidator;
import nexxus.shared.validators.SchemaValidationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VMExecuteImpl implements VMExecuteService {

  @Value("${api.backend-server-url}")
  private String backendServerUrl;

  @Value("${api.prefix}")
  private String apiPrefix;

  private final PspService pspService;
  private final PspOperationsService pspOperationsService;
  private final FlowDefinitionService flowDefinitionService;
  private final FlowActionService flowActionService;
  private final EnvironmentService environmentService;
  private final DenoVMService denoVMService;
  private final CryptoUtil cryptoUtil;
  private final CredentialSanitizer credentialSanitizer;
  private final JsonSchemaAndPayloadValidator schemaValidator;
  private final ObjectMapper objectMapper;

  @Override
  public DenoVMResult executeVmRequest(VmExecutionDto requestDto) {
    log.info("Executing VM execution for requestDto: {}", requestDto);
    Psp psp = pspService.getPspIfEnabled(requestDto.getPspId());
    PspOperation pspOperation =
        pspOperationsService.getPspOperationIfEnabled(psp.getId(), requestDto.getFlowActionId());
    EnvironmentDto environment = environmentService.read(requestDto.getEnvironmentId());

    Map<String, Object> data = requestDto.getExecutePayload();

    log.info("Executing VM execution for data: {}", data);

    try {
      return executeDenoVm(
          pspOperation.getFlowDefinitionId(),
          requestDto.getPspId(),
          requestDto.getTransactionId(),
          requestDto.getStep(),
          data,
          environment.getToken());
    } catch (Exception e) {
      log.error(
          "Error processing wallet action for step: {}, pspId: {}",
          requestDto.getStep(),
          requestDto.getPspId(),
          e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process wallet action: " + e.getMessage());
    }
  }

  private DenoVMResult executeDenoVm(
      String flowDefinitionId,
      String pspId,
      String transactionId,
      String step,
      Map<String, Object> data,
      String token) {

    FlowDefinitionDto flowDefinition = flowDefinitionService.read(flowDefinitionId);
    Psp psp = pspService.getPspIfEnabled(pspId);
    JsonNode credentialJson = decryptCredentialJsonNode(psp.getCredential());

    FlowActionDto flowAction = flowActionService.read(flowDefinition.getFlowActionId());

    DenoVMResult inputValidationResult = validateInputData(flowAction.getInputSchema(), step, data);
    if (inputValidationResult != null && !inputValidationResult.isSuccess()) {
      return inputValidationResult;
    }

    Map<String, String> credentials = convertJsonNodeToMap(credentialJson);

    EnvironmentDto environment = environmentService.read(psp.getEnvironmentId());

    String successRedirectUrl = environment.getSuccessRedirectUrl();
    String failureRedirectUrl = environment.getFailureRedirectUrl();

    DenoVMRequest.DenoVMUrls urls =
        buildDenoVMUrls(token, transactionId, successRedirectUrl, failureRedirectUrl);

    DenoVMRequest request =
        new DenoVMRequest(transactionId, flowDefinition.getCode(), credentials, data, urls, step);

    DenoVMResult result = denoVMService.executeCode(request);

    DenoVMResult outputValidationResult =
        validateOutputData(flowAction.getOutputSchema(), step, result);
    if (outputValidationResult != null && !outputValidationResult.isSuccess()) {
      return outputValidationResult;
    }

    return sanitizeDenoVMResult(result, credentialJson);
  }

  private DenoVMRequest.DenoVMUrls buildDenoVMUrls(
      String token, String transactionId, String successRedirectUrl, String failureRedirectUrl) {
    DenoVMRequest.DenoVMUrl serverUrls =
        new DenoVMRequest.DenoVMUrl(
            backendServerUrl
                + apiPrefix
                + "/external/inbound/r/redirect/"
                + token
                + "/"
                + transactionId,
            backendServerUrl
                + apiPrefix
                + "/external/inbound/w/webhook/"
                + token
                + "/"
                + transactionId,
            successRedirectUrl,
            failureRedirectUrl);
    return new DenoVMRequest.DenoVMUrls(serverUrls);
  }

  private Map<String, String> convertJsonNodeToMap(JsonNode jsonNode) {
    Map<String, String> credentials = new java.util.HashMap<>();

    if (jsonNode != null && jsonNode.isObject()) {
      jsonNode
          .fieldNames()
          .forEachRemaining(
              fieldName -> {
                JsonNode value = jsonNode.get(fieldName);
                if (value.isTextual()) {
                  credentials.put(fieldName, value.asText());
                } else {
                  credentials.put(fieldName, value.toString());
                }
              });
    }

    return credentials;
  }

  private JsonNode decryptCredentialJsonNode(JsonNode credentialJson) {
    try {
      return cryptoUtil.decryptCredentialJsonNode(credentialJson);
    } catch (Exception e) {
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.TOKEN_DECRYPTION_FAILED.getCode());
    }
  }

  private DenoVMResult sanitizeDenoVMResult(DenoVMResult result, JsonNode credentialJson) {
    Map<String, String> credentialValues =
        credentialSanitizer.convertCredentialJsonToMap(credentialJson);

    // Object sanitizedData =
    //     result.getData() != null
    //         ? credentialSanitizer.sanitizeCredentials(result.getData(), credentialValues)
    //         : null;

    String sanitizedError = null;
    if (result.getError() != null) {
      sanitizedError =
          (String) credentialSanitizer.sanitizeCredentials(result.getError(), credentialValues);
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> sanitizedMeta =
        result.getMeta() != null
            ? (Map<String, Object>)
                credentialSanitizer.sanitizeCredentials(result.getMeta(), credentialValues)
            : null;

    return new DenoVMResult(result.isSuccess(), result.getData(), sanitizedError, sanitizedMeta);
  }

  private DenoVMResult validateInputData(
      String inputSchemaJson, String step, Map<String, Object> data) {
    Object bodyData = data != null ? data.get("body") : null;
    if (bodyData == null) {
      bodyData = data;
    }
    return validateSchema(inputSchemaJson, step, bodyData, "Input", "input schema");
  }

  private DenoVMResult validateOutputData(
      String outputSchemaJson, String step, DenoVMResult result) {
    if (!result.isSuccess()) {
      return null;
    }

    Object dataToValidate = result.getData();
    if (dataToValidate instanceof Map) {
      @SuppressWarnings("unchecked")
      Map<String, Object> dataMap = (Map<String, Object>) dataToValidate;
      if (dataMap.containsKey("data")) {
        dataToValidate = dataMap.get("data");
      }
    }

    return validateSchema(outputSchemaJson, step, dataToValidate, "Output", "output schema");
  }

  private DenoVMResult validateSchema(
      String schemaJson,
      String step,
      Object dataToValidate,
      String validationType,
      String schemaType) {
    String stepSchemaJson = extractStepSchemaJson(schemaJson, step);
    if (stepSchemaJson == null) {
      return DenoVMResult.error(
          validationType
              + " schema validation failed: Schema definition is required for step '"
              + step
              + "' but not found in "
              + schemaType);
    }

    try {
      String dataJson = objectMapper.writeValueAsString(dataToValidate);
      schemaValidator.validateAndThrow(stepSchemaJson, dataJson);
      return null;
    } catch (SchemaValidationException e) {
      return DenoVMResult.error(
          validationType + " validation failed for step '" + step + "': " + e.getMessage());
    } catch (Exception e) {
      return DenoVMResult.error("Failed to validate " + schemaType + ": " + e.getMessage());
    }
  }

  private String extractStepSchemaJson(String rootSchemaJson, String step) {
    try {
      JsonNode rootSchema = objectMapper.readTree(rootSchemaJson);
      if (rootSchema == null || !rootSchema.isObject() || !rootSchema.has(step)) {
        return null;
      }
      JsonNode stepSchema = rootSchema.get(step);
      if (stepSchema == null || !stepSchema.isObject()) {
        return null;
      }

      JsonNode modifiedSchema = stepSchema.deepCopy();
      if (modifiedSchema.isObject()) {
        ((ObjectNode) modifiedSchema).put("additionalProperties", true);
      }

      return modifiedSchema.toString();
    } catch (Exception e) {
      return null;
    }
  }
}
