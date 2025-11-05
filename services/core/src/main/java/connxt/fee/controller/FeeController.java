package connxt.fee.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import connxt.fee.dto.FeeDto;
import connxt.fee.service.FeeService;
import connxt.permission.annotations.RequiresPermission;
import connxt.permission.annotations.RequiresScope;
import connxt.shared.builder.ResponseBuilder;
import connxt.shared.builder.dto.ApiResponse;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/fees")
@RequiredArgsConstructor
@Validated
@RequiresScope({"SYSTEM", "FI", "BRAND"})
public class FeeController {

  private final FeeService feeService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  @RequiresPermission(module = "fees", action = "create")
  public ResponseEntity<ApiResponse<Object>> create(@Validated @RequestBody FeeDto feeDto) {
    log.info("Received request to create fee: {}", feeDto.getName());
    return responseBuilder.successResponse(feeService.create(feeDto));
  }

  @GetMapping("/{id}")
  @RequiresPermission(module = "fees", action = "read")
  public ResponseEntity<ApiResponse<Object>> readLatest(@PathVariable("id") @NotBlank String id) {
    log.info("Received request to retrieve fee with ID: {}", id);
    return responseBuilder.successResponse(feeService.readLatest(id));
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}")
  @RequiresPermission(module = "fees", action = "read")
  public ResponseEntity<ApiResponse<Object>> readByBrandAndEnvironment(
      @PathVariable("brandId") @NotBlank String brandId,
      @PathVariable("environmentId") @NotBlank String environmentId) {
    log.info(
        "Received request to retrieve fees for brand: {} and environment: {}",
        brandId,
        environmentId);
    return responseBuilder.successResponse(
        feeService.readByBrandAndEnvironment(brandId, environmentId));
  }

  @GetMapping("/psp/{pspId}")
  @RequiresPermission(module = "fees", action = "read")
  public ResponseEntity<ApiResponse<Object>> readByPspId(
      @PathVariable("pspId") @NotBlank String pspId) {
    log.info("Received request to retrieve fees for PSP: {}", pspId);
    return responseBuilder.successResponse(feeService.readByPspId(pspId));
  }

  @PutMapping("/{id}")
  @RequiresPermission(module = "fees", action = "update")
  public ResponseEntity<ApiResponse<Object>> update(
      @PathVariable("id") @NotBlank String id, @Validated @RequestBody FeeDto feeDto) {
    log.info("Received request to update fee with ID: {}", id);
    feeDto.setId(id);
    return responseBuilder.successResponse(feeService.update(id, feeDto));
  }

  @DeleteMapping("/{id}")
  @RequiresPermission(module = "fees", action = "delete")
  public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") @NotBlank String id) {
    log.info("Received request to delete fee with ID: {}", id);
    feeService.delete(id);
    return responseBuilder.successResponse("Fee deleted successfully");
  }
}
