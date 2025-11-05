package connxt.psp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import connxt.psp.dto.PspDto;
import connxt.psp.dto.UpdatePspDto;
import connxt.psp.service.PspService;
import connxt.shared.builder.ResponseBuilder;
import connxt.shared.builder.dto.ApiResponse;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/psps")
@RequiredArgsConstructor
@Validated
public class PspController {

  private final PspService pspService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  public ResponseEntity<ApiResponse<Object>> create(@Validated @RequestBody PspDto pspDto) {
    return responseBuilder.successResponse(pspService.create(pspDto));
  }

  @GetMapping("/{pspId}")
  public ResponseEntity<ApiResponse<Object>> getById(
      @Validated @PathVariable @NotBlank String pspId) {
    return responseBuilder.successResponse(pspService.getById(pspId));
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}")
  public ResponseEntity<ApiResponse<Object>> getByBrandAndEnvironment(
      @Validated @PathVariable @NotBlank String brandId,
      @Validated @PathVariable @NotBlank String environmentId) {
    return responseBuilder.successResponse(
        pspService.getByBrandAndEnvironment(brandId, environmentId));
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}/{flowActionId}/{status}/{currency}")
  public ResponseEntity<ApiResponse<Object>>
      getByBrandAndEnvironmentByStatusAndCurrencyAndFlowAction(
          @Validated @PathVariable @NotBlank String brandId,
          @Validated @PathVariable @NotBlank String environmentId,
          @Validated @PathVariable @NotBlank String flowActionId,
          @Validated @PathVariable @NotBlank String status,
          @Validated @PathVariable @NotBlank String currency) {
    return responseBuilder.successResponse(
        pspService.getByBrandAndEnvironmentByStatusAndCurrencyAndFlowAction(
            brandId, environmentId, status, currency, flowActionId));
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}/{flowActionId}/{status}")
  public ResponseEntity<ApiResponse<Object>> getByBrandAndEnvironmentByStatusAndFlowAction(
      @Validated @PathVariable @NotBlank String brandId,
      @Validated @PathVariable @NotBlank String environmentId,
      @Validated @PathVariable @NotBlank String flowActionId,
      @Validated @PathVariable @NotBlank String status) {
    return responseBuilder.successResponse(
        pspService.getByBrandAndEnvironmentByStatusAndFlowAction(
            brandId, environmentId, status, flowActionId));
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}/currencies")
  public ResponseEntity<ApiResponse<Object>> getSupportedCurrenciesByBrandAndEnvironment(
      @Validated @PathVariable @NotBlank String brandId,
      @Validated @PathVariable @NotBlank String environmentId) {
    return responseBuilder.successResponse(
        pspService.getSupportedCurrenciesByBrandAndEnvironment(brandId, environmentId));
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}/countries")
  public ResponseEntity<ApiResponse<Object>> getSupportedCountriesByBrandAndEnvironment(
      @Validated @PathVariable @NotBlank String brandId,
      @Validated @PathVariable @NotBlank String environmentId) {
    return responseBuilder.successResponse(
        pspService.getSupportedCountriesByBrandAndEnvironment(brandId, environmentId));
  }

  @PutMapping("/{pspId}")
  public ResponseEntity<ApiResponse<Object>> update(
      @Validated @PathVariable @NotBlank String pspId,
      @Validated @RequestBody UpdatePspDto pspDto) {
    return responseBuilder.successResponse(pspService.update(pspId, pspDto));
  }

  @PutMapping("/{pspId}/{status}")
  public ResponseEntity<ApiResponse<Object>> updateStatus(
      @Validated @PathVariable @NotBlank String pspId,
      @Validated @PathVariable @NotBlank String status) {
    return responseBuilder.successResponse(pspService.updateStatus(pspId, status));
  }
}
