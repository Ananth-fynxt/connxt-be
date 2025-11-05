package connxt.pspgroup.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import connxt.permission.annotations.RequiresPermission;
import connxt.permission.annotations.RequiresScope;
import connxt.pspgroup.dto.PspGroupDto;
import connxt.pspgroup.service.PspGroupService;
import connxt.shared.builder.ResponseBuilder;
import connxt.shared.builder.dto.ApiResponse;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/psp-groups")
@RequiredArgsConstructor
@Validated
@RequiresScope({"SYSTEM", "FI", "BRAND"})
public class PspGroupController {

  private final PspGroupService pspGroupService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  @RequiresPermission(module = "psp_groups", action = "create")
  public ResponseEntity<ApiResponse<Object>> create(
      @Validated @RequestBody PspGroupDto pspGroupDto) {
    log.info("Received request to create PSP group: {}", pspGroupDto.getName());
    return responseBuilder.successResponse(pspGroupService.create(pspGroupDto));
  }

  @GetMapping("/{id}")
  @RequiresPermission(module = "psp_groups", action = "read")
  public ResponseEntity<ApiResponse<Object>> readLatest(@PathVariable("id") @NotBlank String id) {
    log.info("Received request to retrieve PSP group with ID: {}", id);
    return responseBuilder.successResponse(pspGroupService.readLatest(id));
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}")
  @RequiresPermission(module = "psp_groups", action = "read")
  public ResponseEntity<ApiResponse<Object>> readByBrandAndEnvironment(
      @PathVariable("brandId") @NotBlank String brandId,
      @PathVariable("environmentId") @NotBlank String environmentId) {
    log.info(
        "Received request to retrieve PSP groups for brand: {} and environment: {}",
        brandId,
        environmentId);
    return responseBuilder.successResponse(
        pspGroupService.readByBrandAndEnvironment(brandId, environmentId));
  }

  @GetMapping("/psp/{pspId}")
  @RequiresPermission(module = "psp_groups", action = "read")
  public ResponseEntity<ApiResponse<Object>> readByPspId(
      @PathVariable("pspId") @NotBlank String pspId) {
    log.info("Received request to retrieve PSP groups for PSP: {}", pspId);
    return responseBuilder.successResponse(pspGroupService.readByPspId(pspId));
  }

  @PutMapping("/{id}")
  @RequiresPermission(module = "psp_groups", action = "update")
  public ResponseEntity<ApiResponse<Object>> update(
      @PathVariable("id") @NotBlank String id, @Validated @RequestBody PspGroupDto pspGroupDto) {
    log.info("Received request to update PSP group with ID: {}", id);
    pspGroupDto.setId(id);
    return responseBuilder.successResponse(pspGroupService.update(id, pspGroupDto));
  }

  @DeleteMapping("/{id}")
  @RequiresPermission(module = "psp_groups", action = "delete")
  public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") @NotBlank String id) {
    log.info("Received request to delete PSP group with ID: {}", id);
    pspGroupService.delete(id);
    return responseBuilder.successResponse("PSP group deleted successfully");
  }
}
