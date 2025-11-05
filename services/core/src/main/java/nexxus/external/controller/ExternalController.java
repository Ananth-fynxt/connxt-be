package nexxus.external.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import nexxus.external.service.ExternalService;
import nexxus.shared.builder.ResponseBuilder;
import nexxus.shared.builder.dto.ApiResponse;
import nexxus.shared.constants.IdPrefix;
import nexxus.shared.util.RequestBodyExtractor;
import nexxus.shared.util.ValidationUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/external")
@RequiredArgsConstructor
@Validated
public class ExternalController {

  private final ExternalService externalService;
  private final ResponseBuilder responseBuilder;
  private final RequestBodyExtractor requestBodyExtractor;

  @PostMapping("/inbound/r/{step}/{token}/{tnxId}")
  public RedirectView handleReadPost(
      HttpServletRequest request,
      @RequestParam(required = false) Map<String, Object> queryParams,
      @PathVariable("token") @NotBlank String token,
      @PathVariable("tnxId") @NotBlank String tnxId,
      @PathVariable("step") @NotBlank String step) {
    Map<String, Object> requestBody = requestBodyExtractor.extractRequestBody(request);

    return processRedirectRequest(requestBody, queryParams, token, tnxId, step);
  }

  @PostMapping("/inbound/r/{step}/{token}")
  public RedirectView handleReadPostNoTnxId(
      HttpServletRequest request,
      @RequestParam(required = false) Map<String, Object> queryParams,
      @PathVariable("token") @NotBlank String token,
      @PathVariable("step") @NotBlank String step) {
    Map<String, Object> requestBody = requestBodyExtractor.extractRequestBody(request);

    String txnId = extractTransactionId(null, queryParams);

    return processRedirectRequest(requestBody, queryParams, token, txnId, step);
  }

  @GetMapping("/inbound/r/{step}/{token}/{tnxId}")
  public RedirectView handleReadGet(
      @RequestParam(required = false) Map<String, Object> queryParams,
      @PathVariable("token") @NotBlank String token,
      @PathVariable("tnxId") @NotBlank String tnxId,
      @PathVariable("step") @NotBlank String step) {
    return processRedirectRequest(null, queryParams, token, tnxId, step);
  }

  @GetMapping("/inbound/r/{step}/{token}")
  public RedirectView handleReadGetNoTnxId(
      @RequestParam(required = false) Map<String, Object> queryParams,
      @PathVariable("token") @NotBlank String token,
      @PathVariable("step") @NotBlank String step) {
    String txnId = extractTransactionId(null, queryParams);

    return processRedirectRequest(null, queryParams, token, txnId, step);
  }

  @PostMapping("/inbound/w/{step}/{token}/{tnxId}")
  public ResponseEntity<ApiResponse<Object>> handleWritePost(
      HttpServletRequest request,
      @RequestParam(required = false) Map<String, Object> queryParams,
      @PathVariable("step") @NotBlank String step,
      @PathVariable("token") @NotBlank String token,
      @PathVariable("tnxId") @NotBlank String tnxId) {
    Map<String, Object> requestBody = requestBodyExtractor.extractRequestBody(request);

    return processInboundRequest(requestBody, queryParams, token, tnxId, step);
  }

  @PostMapping("/inbound/w/{step}/{token}") // set the webhook URL in the PSP dashboard
  public ResponseEntity<ApiResponse<Object>> handleWritePostNoTnxId(
      HttpServletRequest request,
      @RequestParam(required = false) Map<String, Object> queryParams,
      @PathVariable("token") @NotBlank String token,
      @PathVariable("step") @NotBlank String step) {
    Map<String, Object> requestBody = requestBodyExtractor.extractRequestBody(request);

    String txnId = extractTransactionId(requestBody, queryParams);

    return processInboundRequest(requestBody, queryParams, token, txnId, step);
  }

  @GetMapping("/inbound/w/{step}/{token}/{tnxId}")
  public ResponseEntity<ApiResponse<Object>> handleWriteGet(
      @RequestParam(required = false) Map<String, Object> queryParams,
      @PathVariable("token") @NotBlank String token,
      @PathVariable("tnxId") @NotBlank String tnxId,
      @PathVariable("step") @NotBlank String step) {
    return processInboundRequest(null, queryParams, token, tnxId, step);
  }

  @GetMapping("/inbound/w/{step}/{token}")
  public ResponseEntity<ApiResponse<Object>> handleWriteGetNoTnxId(
      @RequestParam(required = false) Map<String, Object> queryParams,
      @PathVariable("token") @NotBlank String token,
      @PathVariable("step") @NotBlank String step) {
    String txnId = extractTransactionId(null, queryParams);

    return processInboundRequest(null, queryParams, token, txnId, step);
  }

  private RedirectView processRedirectRequest(
      Map<String, Object> requestBody,
      Map<String, Object> queryParams,
      String token,
      String tnxId,
      String step) {
    Map<String, Object> externalDto =
        buildExternalDto(token, tnxId, step, requestBody, queryParams);

    Object result = externalService.read(externalDto);

    String redirectUrl = externalService.extractRedirectUrl(result, token, tnxId, step);

    if (redirectUrl == null) {
      redirectUrl = externalService.getEnvironmentOrigin(tnxId);
    }

    RedirectView redirectView = new RedirectView(redirectUrl);

    redirectView.setStatusCode(org.springframework.http.HttpStatus.FOUND);

    return redirectView;
  }

  private Map<String, Object> buildExternalDto(
      String token,
      String tnxId,
      String step,
      Map<String, Object> requestBody,
      Map<String, Object> queryParams) {

    Map<String, Object> externalDto = new java.util.HashMap<>();
    externalDto.put("token", token);
    externalDto.put("step", step);

    if (ValidationUtils.isNotNullOrEmpty(tnxId)) {
      externalDto.put("tnxId", tnxId);
    }

    if (ValidationUtils.isNotNullOrEmpty(requestBody)) {
      externalDto.put("body", requestBody);
    }

    if (ValidationUtils.isNotNullOrEmpty(queryParams)) {
      externalDto.put("query", queryParams);
    }

    return externalDto;
  }

  private ResponseEntity<ApiResponse<Object>> processInboundRequest(
      Map<String, Object> requestBody,
      Map<String, Object> queryParams,
      String token,
      String tnxId,
      String step) {
    Map<String, Object> externalDto =
        buildExternalDto(token, tnxId, step, requestBody, queryParams);

    return responseBuilder.successResponse(externalService.read(externalDto));
  }

  private String extractTransactionId(
      Map<String, Object> requestBody, Map<String, Object> queryParams) {
    // First, check query parameters
    if (ValidationUtils.isNotNullOrEmpty(queryParams)) {
      String txnId = ValidationUtils.findValueByPrefix(queryParams, IdPrefix.TRANSACTION);
      if (ValidationUtils.isNotNullOrEmpty(txnId)) {
        return txnId;
      }
    }

    // Then, check request body
    if (ValidationUtils.isNotNullOrEmpty(requestBody)) {
      String txnId = ValidationUtils.findValueByPrefix(requestBody, IdPrefix.TRANSACTION);
      if (ValidationUtils.isNotNullOrEmpty(txnId)) {
        return txnId;
      }
    }

    return null;
  }
}
