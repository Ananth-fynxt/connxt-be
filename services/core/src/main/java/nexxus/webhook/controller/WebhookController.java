package nexxus.webhook.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import nexxus.permission.annotations.RequiresPermission;
import nexxus.permission.annotations.RequiresScope;
import nexxus.shared.builder.ResponseBuilder;
import nexxus.shared.builder.dto.ApiResponse;
import nexxus.webhook.dto.WebhookDto;
import nexxus.webhook.service.WebhookService;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
@Validated
@RequiresScope({"SYSTEM", "FI", "BRAND"})
public class WebhookController {

  private final WebhookService webhookService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  @RequiresPermission(module = "webhooks", action = "create")
  public ResponseEntity<ApiResponse<Object>> create(@Validated @RequestBody WebhookDto webhookDto) {
    log.info("Received request to create webhook for status type: {}", webhookDto.getStatusType());
    return responseBuilder.successResponse(webhookService.create(webhookDto));
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}")
  @RequiresPermission(module = "webhooks", action = "read")
  public ResponseEntity<ApiResponse<Object>> readAll(
      @PathVariable("brandId") @NotBlank String brandId,
      @PathVariable("environmentId") @NotBlank String environmentId) {
    log.info("Received request to retrieve all webhooks");
    return responseBuilder.successResponse(
        webhookService.readAll(brandId, environmentId), "Webhooks retrieved successfully");
  }

  @GetMapping("/{id}")
  @RequiresPermission(module = "webhooks", action = "read")
  public ResponseEntity<ApiResponse<Object>> read(@PathVariable("id") @NotBlank String id) {
    log.info("Received request to retrieve webhook with ID: {}", id);
    return responseBuilder.successResponse(webhookService.read(id));
  }

  @PutMapping("/{id}")
  @RequiresPermission(module = "webhooks", action = "update")
  public ResponseEntity<ApiResponse<Object>> update(
      @PathVariable("id") @NotBlank String id, @Validated @RequestBody WebhookDto webhookDto) {
    log.info("Received request to update webhook with ID: {}", id);
    webhookDto.setId(id);
    return responseBuilder.successResponse(webhookService.update(id, webhookDto));
  }

  @DeleteMapping("/{id}")
  @RequiresPermission(module = "webhooks", action = "delete")
  public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") @NotBlank String id) {
    log.info("Received request to delete webhook with ID: {}", id);
    webhookService.delete(id);
    return responseBuilder.successResponse("Webhook deleted successfully");
  }
}
