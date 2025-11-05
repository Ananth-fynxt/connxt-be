package connxt.riskrule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import connxt.permission.annotations.RequiresPermission;
import connxt.permission.annotations.RequiresScope;
import connxt.riskrule.dto.RiskRuleDto;
import connxt.riskrule.service.RiskRuleService;
import connxt.shared.builder.ResponseBuilder;
import connxt.shared.builder.dto.ApiResponse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("risk-rules")
@RequiredArgsConstructor
@Validated
@RequiresScope({"SYSTEM", "FI", "BRAND"})
public class RiskRuleController {

  private final RiskRuleService riskRuleService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  @RequiresPermission(module = "risk_rules", action = "create")
  public ResponseEntity<ApiResponse<Object>> create(
      @Validated @RequestBody RiskRuleDto riskRuleDto) {
    return responseBuilder.successResponse(riskRuleService.create(riskRuleDto));
  }

  @GetMapping
  @RequiresPermission(module = "risk_rules", action = "read")
  public ResponseEntity<ApiResponse<Object>> readAll() {
    return responseBuilder.successResponse(riskRuleService.readAll());
  }

  @GetMapping("/{id}")
  @RequiresPermission(module = "risk_rules", action = "read")
  public ResponseEntity<ApiResponse<Object>> readLatest(@PathVariable("id") @NotBlank String id) {
    return responseBuilder.successResponse(riskRuleService.readLatest(id));
  }

  @GetMapping("/{id}/version/{version}")
  @RequiresPermission(module = "risk_rules", action = "read")
  public ResponseEntity<ApiResponse<Object>> read(
      @PathVariable("id") @NotBlank String id, @PathVariable("version") @NotNull Integer version) {
    return responseBuilder.successResponse(riskRuleService.read(id, version));
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}")
  @RequiresPermission(module = "risk_rules", action = "read")
  public ResponseEntity<ApiResponse<Object>> readByBrandAndEnvironment(
      @PathVariable("brandId") @NotBlank String brandId,
      @PathVariable("environmentId") @NotBlank String environmentId) {
    return responseBuilder.successResponse(
        riskRuleService.readByBrandAndEnvironment(brandId, environmentId));
  }

  @GetMapping("/psp/{pspId}")
  @RequiresPermission(module = "risk_rules", action = "read")
  public ResponseEntity<ApiResponse<Object>> readByPspId(
      @PathVariable("pspId") @NotBlank String pspId) {
    return responseBuilder.successResponse(riskRuleService.readByPspId(pspId));
  }

  @PutMapping("/{id}")
  @RequiresPermission(module = "risk_rules", action = "update")
  public ResponseEntity<ApiResponse<Object>> update(
      @PathVariable("id") @NotBlank String id, @Validated @RequestBody RiskRuleDto riskRuleDto) {
    return responseBuilder.successResponse(riskRuleService.update(id, riskRuleDto));
  }

  @DeleteMapping("/{id}")
  @RequiresPermission(module = "risk_rules", action = "delete")
  public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") @NotBlank String id) {
    riskRuleService.delete(id);
    return responseBuilder.successResponse("Risk Rule deleted successfully");
  }
}
