package connxt.brandcustomer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import connxt.brandcustomer.dto.BrandCustomerDto;
import connxt.brandcustomer.service.BrandCustomerService;
import connxt.permission.annotations.RequiresPermission;
import connxt.permission.annotations.RequiresScope;
import connxt.shared.builder.ResponseBuilder;
import connxt.shared.builder.dto.ApiResponse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/brand-customers")
@RequiredArgsConstructor
@Validated
public class BrandCustomerController {

  private final BrandCustomerService brandCustomerService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  @RequiresScope({"SYSTEM", "FI", "BRAND"})
  @RequiresPermission(module = "customers", action = "create")
  public ResponseEntity<ApiResponse<Object>> create(
      @Validated @RequestBody @NotNull BrandCustomerDto dto) {
    log.info("Received request to create brand customer: {}", dto.getName());
    return responseBuilder.successResponse(brandCustomerService.create(dto));
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}")
  @RequiresScope({"SYSTEM", "FI", "BRAND"})
  @RequiresPermission(module = "customers", action = "read")
  public ResponseEntity<ApiResponse<Object>> readAll(
      @PathVariable("brandId") @NotBlank String brandId,
      @PathVariable("environmentId") @NotBlank String environmentId) {
    log.info(
        "Received request to retrieve all brand customers for brandId: {} and environmentId: {}",
        brandId,
        environmentId);
    return responseBuilder.successResponse(
        brandCustomerService.readAll(brandId, environmentId),
        "Brand customers retrieved successfully");
  }

  @GetMapping("/{id}")
  @RequiresScope({"SYSTEM", "FI", "BRAND", "EXTERNAL"})
  @RequiresPermission(module = "customers", action = "read")
  public ResponseEntity<ApiResponse<Object>> read(
      @PathVariable("id") @Validated @NotBlank String id) {
    log.info("Received request to retrieve brand customer with ID: {}", id);
    return responseBuilder.successResponse(brandCustomerService.read(id));
  }

  @PutMapping("/{id}")
  @RequiresScope({"SYSTEM", "FI", "BRAND"})
  @RequiresPermission(module = "customers", action = "update")
  public ResponseEntity<ApiResponse<Object>> update(
      @NotBlank @PathVariable String id,
      @Validated @NotNull @RequestBody BrandCustomerDto brandCustomerDto) {
    log.info("Received request to update brand customer with ID: {}", id);
    brandCustomerDto.setId(id);
    return responseBuilder.successResponse(brandCustomerService.update(brandCustomerDto));
  }
}
