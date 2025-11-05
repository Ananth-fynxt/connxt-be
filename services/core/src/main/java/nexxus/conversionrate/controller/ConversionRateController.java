package nexxus.conversionrate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import nexxus.conversionrate.dto.ConversionRateDto;
import nexxus.conversionrate.service.ConversionRateService;
import nexxus.permission.annotations.RequiresPermission;
import nexxus.permission.annotations.RequiresScope;
import nexxus.shared.builder.ResponseBuilder;
import nexxus.shared.builder.dto.ApiResponse;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/conversion-rates")
@RequiredArgsConstructor
@Validated
@RequiresScope({"SYSTEM", "FI", "BRAND"})
public class ConversionRateController {

  private final ConversionRateService conversionRateService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  @RequiresPermission(module = "conversion_rates", action = "create")
  public ResponseEntity<ApiResponse<Object>> createRate(
      @Validated @RequestBody ConversionRateDto conversionRateDto) {
    log.info(
        "Received request to create conversion rate: {} to {} for brand: {} and environment: {}",
        conversionRateDto.getSourceCurrency(),
        conversionRateDto.getTargetCurrency(),
        conversionRateDto.getBrandId(),
        conversionRateDto.getEnvironmentId());
    return responseBuilder.successResponse(conversionRateService.createRate(conversionRateDto));
  }

  @GetMapping("/{id}")
  @RequiresPermission(module = "conversion_rates", action = "read")
  public ResponseEntity<ApiResponse<Object>> readLatestRate(
      @PathVariable("id") @NotBlank String id) {
    log.info("Received request to retrieve conversion rate with ID: {}", id);
    return responseBuilder.successResponse(conversionRateService.readLatestRate(id));
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}")
  @RequiresPermission(module = "conversion_rates", action = "read")
  public ResponseEntity<ApiResponse<Object>> readRatesByBrandAndEnvironment(
      @PathVariable("brandId") @NotBlank String brandId,
      @PathVariable("environmentId") @NotBlank String environmentId) {
    log.info(
        "Received request to retrieve conversion rates for brand: {} and environment: {}",
        brandId,
        environmentId);
    return responseBuilder.successResponse(
        conversionRateService.readRatesByBrandAndEnvironment(brandId, environmentId));
  }

  @PutMapping("/{id}")
  @RequiresPermission(module = "conversion_rates", action = "update")
  public ResponseEntity<ApiResponse<Object>> updateRate(
      @PathVariable("id") @NotBlank String id,
      @Validated @RequestBody ConversionRateDto conversionRateDto) {
    log.info("Received request to update conversion rate with ID: {}", id);
    conversionRateDto.setId(id);
    return responseBuilder.successResponse(conversionRateService.updateRate(id, conversionRateDto));
  }

  @DeleteMapping("/{id}")
  @RequiresPermission(module = "conversion_rates", action = "delete")
  public ResponseEntity<ApiResponse<Object>> deleteRate(@PathVariable("id") @NotBlank String id) {
    log.info("Received request to delete conversion rate with ID: {}", id);
    conversionRateService.deleteRate(id);
    return responseBuilder.successResponse("Conversion rate deleted successfully");
  }
}
