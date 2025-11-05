package nexxus.branduser.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import nexxus.branduser.dto.BrandUserDto;
import nexxus.branduser.service.BrandUserService;
import nexxus.permission.annotations.RequiresPermission;
import nexxus.permission.annotations.RequiresScope;
import nexxus.shared.builder.ResponseBuilder;
import nexxus.shared.builder.dto.ApiResponse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/brand-users")
@RequiredArgsConstructor
@Validated
@RequiresScope({"SYSTEM", "FI", "BRAND"})
public class BrandUserController {

  private final BrandUserService brandUserService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  @RequiresPermission(module = "brand_users", action = "create")
  public ResponseEntity<ApiResponse<Object>> create(
      @Validated @RequestBody @NotNull BrandUserDto dto) {
    log.info("Received request to create brand user: {}", dto.getName());
    return responseBuilder.successResponse(brandUserService.create(dto));
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}")
  @RequiresPermission(module = "brand_users", action = "read")
  public ResponseEntity<ApiResponse<Object>> readAll(
      @PathVariable("brandId") @NotBlank String brandId,
      @PathVariable("environmentId") @NotBlank String environmentId) {
    log.info(
        "Received request to retrieve all brand users for brandId: {} and environmentId: {}",
        brandId,
        environmentId);
    return responseBuilder.successResponse(
        brandUserService.readAll(brandId, environmentId), "Brand users retrieved successfully");
  }

  @GetMapping("/{id}")
  @RequiresPermission(module = "brand_users", action = "read")
  public ResponseEntity<ApiResponse<Object>> read(
      @PathVariable("id") @Validated @NotBlank String id) {
    log.info("Received request to retrieve brand user with ID: {}", id);
    return responseBuilder.successResponse(brandUserService.read(id));
  }

  @PutMapping("/{id}")
  @RequiresPermission(module = "brand_users", action = "update")
  public ResponseEntity<ApiResponse<Object>> update(
      @NotBlank @PathVariable String id,
      @Validated @NotNull @RequestBody BrandUserDto brandUserDto) {
    log.info("Received request to update brand user with ID: {}", id);
    brandUserDto.setId(id);
    return responseBuilder.successResponse(brandUserService.update(brandUserDto));
  }

  @DeleteMapping("/{id}")
  @RequiresPermission(module = "brand_users", action = "delete")
  public ResponseEntity<ApiResponse<Object>> delete(@NotBlank @PathVariable("id") String id) {
    log.info("Received request to delete brand user with ID: {}", id);
    brandUserService.delete(id);
    return responseBuilder.successResponse("Brand user deleted successfully");
  }
}
