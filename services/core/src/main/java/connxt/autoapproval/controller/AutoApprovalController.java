package connxt.autoapproval.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import connxt.autoapproval.dto.AutoApprovalDto;
import connxt.autoapproval.service.AutoApprovalService;
import connxt.permission.annotations.RequiresPermission;
import connxt.permission.annotations.RequiresScope;
import connxt.shared.builder.ResponseBuilder;
import connxt.shared.builder.dto.ApiResponse;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auto-approvals")
@RequiredArgsConstructor
@Validated
@RequiresScope({"SYSTEM", "FI", "BRAND"})
public class AutoApprovalController {

  private final AutoApprovalService autoApprovalService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  @RequiresPermission(module = "auto_approvals", action = "create")
  public ResponseEntity<ApiResponse<Object>> create(
      @Validated @RequestBody AutoApprovalDto autoApprovalDto) {
    log.info("Received request to create auto approval: {}", autoApprovalDto.getName());
    return responseBuilder.successResponse(autoApprovalService.create(autoApprovalDto));
  }

  @GetMapping("/{id}")
  @RequiresPermission(module = "auto_approvals", action = "read")
  public ResponseEntity<ApiResponse<Object>> readLatest(@PathVariable("id") @NotBlank String id) {
    log.info("Received request to retrieve auto approval with ID: {}", id);
    return responseBuilder.successResponse(autoApprovalService.readLatest(id));
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}")
  @RequiresPermission(module = "auto_approvals", action = "read")
  public ResponseEntity<ApiResponse<Object>> readByBrandAndEnvironment(
      @PathVariable("brandId") @NotBlank String brandId,
      @PathVariable("environmentId") @NotBlank String environmentId) {
    log.info(
        "Received request to retrieve auto approvals for brand: {} and environment: {}",
        brandId,
        environmentId);
    return responseBuilder.successResponse(
        autoApprovalService.readByBrandAndEnvironment(brandId, environmentId));
  }

  @GetMapping("/psp/{pspId}")
  @RequiresPermission(module = "auto_approvals", action = "read")
  public ResponseEntity<ApiResponse<Object>> readByPspId(
      @PathVariable("pspId") @NotBlank String pspId) {
    log.info("Received request to retrieve auto approvals for PSP: {}", pspId);
    return responseBuilder.successResponse(autoApprovalService.readByPspId(pspId));
  }

  @PutMapping("/{id}")
  @RequiresPermission(module = "auto_approvals", action = "update")
  public ResponseEntity<ApiResponse<Object>> update(
      @PathVariable("id") @NotBlank String id,
      @Validated @RequestBody AutoApprovalDto autoApprovalDto) {
    log.info("Received request to update auto approval with ID: {}", id);
    autoApprovalDto.setId(id);
    return responseBuilder.successResponse(autoApprovalService.update(id, autoApprovalDto));
  }

  @DeleteMapping("/{id}")
  @RequiresPermission(module = "auto_approvals", action = "delete")
  public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") @NotBlank String id) {
    log.info("Received request to delete auto approval with ID: {}", id);
    autoApprovalService.delete(id);
    return responseBuilder.successResponse("Auto approval deleted successfully");
  }
}
