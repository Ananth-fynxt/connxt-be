package nexxus.brand.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import nexxus.brand.dto.BrandDto;
import nexxus.brand.service.BrandService;
import nexxus.permission.annotations.RequiresScope;
import nexxus.shared.builder.ResponseBuilder;
import nexxus.shared.builder.dto.ApiResponse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
@Validated
@RequiresScope({"SYSTEM", "FI", "BRAND"})
public class BrandController {

  private final BrandService brandService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  public ResponseEntity<ApiResponse<Object>> create(
      @Validated @RequestBody @NotNull BrandDto brandDto) {
    log.info("Received request to create brand: {}", brandDto.getName());
    return responseBuilder.successResponse(brandService.create(brandDto));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<Object>> readAll() {
    log.info("Received request to retrieve all brands");
    return responseBuilder.successResponse(brandService.readAll(), "Brands retrieved successfully");
  }

  @GetMapping("/by-fi/{fiId}")
  public ResponseEntity<ApiResponse<Object>> readByFiId(
      @PathVariable("fiId") @Validated @NotBlank String fiId) {
    log.info("Received request to retrieve brands for FI ID: {}", fiId);
    return responseBuilder.successResponse(
        brandService.findByFiId(fiId), "Brands retrieved successfully");
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> read(
      @PathVariable("id") @Validated @NotBlank String id) {
    log.info("Received request to retrieve brand with ID: {}", id);
    return responseBuilder.successResponse(brandService.read(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> update(
      @NotBlank @PathVariable String id, @Validated @NotNull @RequestBody BrandDto brandDto) {
    log.info("Received request to update brand with ID: {}", id);
    brandDto.setId(id);
    return responseBuilder.successResponse(brandService.update(brandDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> delete(@NotBlank @PathVariable("id") String id) {
    log.info("Received request to delete brand with ID: {}", id);
    brandService.delete(id);
    return responseBuilder.successResponse("Brand deleted successfully");
  }
}
