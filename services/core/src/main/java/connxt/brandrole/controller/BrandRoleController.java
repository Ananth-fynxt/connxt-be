package connxt.brandrole.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import connxt.brandrole.dto.BrandRoleDto;
import connxt.brandrole.service.BrandRoleService;
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
@RequestMapping("/brand-roles")
@RequiredArgsConstructor
@Validated
@RequiresScope({"SYSTEM", "FI", "BRAND"})
public class BrandRoleController {

  private final BrandRoleService brandRoleService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  @RequiresPermission(module = "brand_roles", action = "create")
  public ResponseEntity<ApiResponse<Object>> create(
      @Validated @RequestBody @NotNull BrandRoleDto brandRoleDto) {
    log.info("Received request to create brand role: {}", brandRoleDto.getName());
    return responseBuilder.successResponse(brandRoleService.create(brandRoleDto));
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}")
  @RequiresPermission(module = "brand_roles", action = "read")
  public ResponseEntity<ApiResponse<Object>> readAll(
      @PathVariable("brandId") @NotBlank String brandId,
      @PathVariable("environmentId") @NotBlank String environmentId) {
    log.info(
        "Received request to retrieve all brand roles for brandId: {} and environmentId: {}",
        brandId,
        environmentId);
    return responseBuilder.successResponse(
        brandRoleService.readAll(brandId, environmentId), "Brand roles retrieved successfully");
  }

  @GetMapping("/{id}")
  @RequiresPermission(module = "brand_roles", action = "read")
  public ResponseEntity<ApiResponse<Object>> read(
      @PathVariable("id") @Validated @NotBlank String id) {
    log.info("Received request to retrieve brand role with ID: {}", id);
    return responseBuilder.successResponse(brandRoleService.read(id));
  }

  @PutMapping("/{id}")
  @RequiresPermission(module = "brand_roles", action = "update")
  public ResponseEntity<ApiResponse<Object>> update(
      @NotBlank @PathVariable String id,
      @Validated @NotNull @RequestBody BrandRoleDto brandRoleDto) {
    log.info("Received request to update brand role with ID: {}", id);
    brandRoleDto.setId(id);
    return responseBuilder.successResponse(brandRoleService.update(brandRoleDto));
  }

  @DeleteMapping("/{id}")
  @RequiresPermission(module = "brand_roles", action = "delete")
  public ResponseEntity<ApiResponse<Object>> delete(@NotBlank @PathVariable("id") String id) {
    log.info("Received request to delete brand role with ID: {}", id);
    brandRoleService.delete(id);
    return responseBuilder.successResponse("Brand role deleted successfully");
  }
}
