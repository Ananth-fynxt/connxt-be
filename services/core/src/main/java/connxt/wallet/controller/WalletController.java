package connxt.wallet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import connxt.permission.annotations.RequiresScope;
import connxt.shared.builder.ResponseBuilder;
import connxt.shared.builder.dto.ApiResponse;
import connxt.wallet.service.WalletService;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
@Validated
@RequiresScope({"SYSTEM", "FI", "BRAND", "EXTERNAL"})
public class WalletController {

  private final WalletService walletService;
  private final ResponseBuilder responseBuilder;

  @GetMapping("/brand/{brandId}/environment/{environmentId}/customer/{customerId}")
  public ResponseEntity<ApiResponse<Object>> walletDetails(
      @PathVariable("brandId") @NotBlank String brandId,
      @PathVariable("environmentId") @NotBlank String environmentId,
      @PathVariable("customerId") @NotBlank String customerId) {
    log.info(
        "Received request to fetch details for brandId: {}, environmentId: {}, customerId: {}",
        brandId,
        environmentId,
        customerId);
    return responseBuilder.successResponse(
        walletService.walletDetails(brandId, environmentId, customerId));
  }
}
