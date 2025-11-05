package connxt.transactionlimit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import connxt.permission.annotations.RequiresPermission;
import connxt.permission.annotations.RequiresScope;
import connxt.shared.builder.ResponseBuilder;
import connxt.shared.builder.dto.ApiResponse;
import connxt.transactionlimit.dto.TransactionLimitDto;
import connxt.transactionlimit.service.TransactionLimitService;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/transaction-limits")
@RequiredArgsConstructor
@Validated
@RequiresScope({"SYSTEM", "FI", "BRAND"})
public class TransactionLimitController {

  private final TransactionLimitService transactionLimitService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  @RequiresPermission(module = "transaction_limits", action = "create")
  public ResponseEntity<ApiResponse<Object>> create(
      @Validated @RequestBody TransactionLimitDto transactionLimitDto) {
    log.info("Received request to create transaction limit: {}", transactionLimitDto.getName());
    return responseBuilder.successResponse(transactionLimitService.create(transactionLimitDto));
  }

  @GetMapping("/{id}")
  @RequiresPermission(module = "transaction_limits", action = "read")
  public ResponseEntity<ApiResponse<Object>> readLatest(@PathVariable("id") @NotBlank String id) {
    log.info("Received request to retrieve transaction limit with ID: {}", id);
    return responseBuilder.successResponse(transactionLimitService.readLatest(id));
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}")
  @RequiresPermission(module = "transaction_limits", action = "read")
  public ResponseEntity<ApiResponse<Object>> readByBrandAndEnvironment(
      @PathVariable("brandId") @NotBlank String brandId,
      @PathVariable("environmentId") @NotBlank String environmentId) {
    log.info(
        "Received request to retrieve transaction limits for brand: {} and environment: {}",
        brandId,
        environmentId);
    return responseBuilder.successResponse(
        transactionLimitService.readByBrandAndEnvironment(brandId, environmentId));
  }

  @GetMapping("/psp/{pspId}")
  @RequiresPermission(module = "transaction_limits", action = "read")
  public ResponseEntity<ApiResponse<Object>> readByPspId(
      @PathVariable("pspId") @NotBlank String pspId) {
    log.info("Received request to retrieve transaction limits for PSP: {}", pspId);
    return responseBuilder.successResponse(transactionLimitService.readByPspId(pspId));
  }

  @PutMapping("/{id}")
  @RequiresPermission(module = "transaction_limits", action = "update")
  public ResponseEntity<ApiResponse<Object>> update(
      @PathVariable("id") @NotBlank String id,
      @Validated @RequestBody TransactionLimitDto transactionLimitDto) {
    log.info("Received request to update transaction limit with ID: {}", id);
    transactionLimitDto.setId(id);
    return responseBuilder.successResponse(transactionLimitService.update(id, transactionLimitDto));
  }

  @DeleteMapping("/{id}")
  @RequiresPermission(module = "transaction_limits", action = "delete")
  public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") @NotBlank String id) {
    log.info("Received request to delete transaction limit with ID: {}", id);
    transactionLimitService.delete(id);
    return responseBuilder.successResponse("Transaction limit deleted successfully");
  }
}
