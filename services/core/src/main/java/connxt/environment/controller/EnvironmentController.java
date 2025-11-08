package connxt.environment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import connxt.environment.dto.EnvironmentDto;
import connxt.environment.service.EnvironmentService;
import connxt.shared.builder.ResponseBuilder;
import connxt.shared.builder.dto.ApiResponse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/environments")
@RequiredArgsConstructor
@Validated
public class EnvironmentController {

  private final EnvironmentService environmentService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  public ResponseEntity<ApiResponse<Object>> create(
      @Validated @RequestBody @NotNull EnvironmentDto environmentDto) {
    log.info("Received request to create environment: {}", environmentDto.getName());
    return responseBuilder.successResponse(environmentService.create(environmentDto));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<Object>> readAll() {
    log.info("Received request to retrieve all environments");
    return responseBuilder.successResponse(
        environmentService.readAll(), "Environments retrieved successfully");
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> read(
      @PathVariable("id") @Validated @NotBlank String id) {
    log.info("Received request to retrieve environment with ID: {}", id);
    return responseBuilder.successResponse(environmentService.read(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> update(
      @NotBlank @PathVariable String id,
      @Validated @NotNull @RequestBody EnvironmentDto environmentDto) {
    log.info("Received request to update environment with ID: {}", id);
    environmentDto.setId(id);
    return responseBuilder.successResponse(environmentService.update(environmentDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> delete(@NotBlank @PathVariable("id") String id) {
    log.info("Received request to delete environment with ID: {}", id);
    environmentService.delete(id);
    return responseBuilder.successResponse("Environment deleted successfully");
  }

  @GetMapping("/brand/{brandId}")
  public ResponseEntity<ApiResponse<Object>> findByBrandId(
      @PathVariable("brandId") @Validated @NotBlank String brandId) {
    log.info("Received request to retrieve environments for brand ID: {}", brandId);
    return responseBuilder.successResponse(
        environmentService.findByBrandId(brandId), "Environments retrieved successfully");
  }

  @PutMapping("/{id}/rotate-secret")
  public ResponseEntity<ApiResponse<Object>> rotateSecret(
      @PathVariable("id") @Validated @NotBlank String id) {
    log.info("Received request to rotate secret for environment with ID: {}", id);
    return responseBuilder.successResponse(
        environmentService.rotateSecret(id), "Environment secret rotated successfully");
  }
}
