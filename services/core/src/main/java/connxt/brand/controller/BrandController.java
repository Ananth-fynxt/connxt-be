package connxt.brand.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import connxt.brand.dto.BrandDto;
import connxt.brand.service.BrandService;
import connxt.shared.builder.ResponseBuilder;
import connxt.shared.builder.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
@Validated
@Tag(
    name = "Brands",
    description =
        "API endpoints for managing brands. Brands represent organizations or companies that use "
            + "the system, with associated environments and configurations.")
public class BrandController {

  private final BrandService brandService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  @Operation(
      summary = "Create a new brand",
      description =
          "Creates a new brand configuration. Brands represent organizations or companies that use "
              + "the system.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Brand created successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid request data or validation error",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing authentication token",
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> create(
      @Parameter(
              description =
                  "Brand configuration details including name, email, and other properties",
              required = true,
              content = @Content(schema = @Schema(implementation = BrandDto.class)))
          @Validated
          @RequestBody
          @NotNull
          BrandDto brandDto) {
    log.info("Received request to create brand: {}", brandDto.getName());
    return responseBuilder.successResponse(brandService.create(brandDto));
  }

  @GetMapping
  @Operation(
      summary = "Get all brands",
      description = "Retrieves all brand configurations in the system.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Brands retrieved successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing authentication token",
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> readAll() {
    log.info("Received request to retrieve all brands");
    return responseBuilder.successResponse(brandService.readAll(), "Brands retrieved successfully");
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Get brand by ID",
      description = "Retrieves a specific brand configuration by its unique identifier.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Brand retrieved successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid brand ID format",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing authentication token",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Brand not found",
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> read(
      @Parameter(
              description = "Unique identifier of the brand",
              required = true,
              example = "brn_001")
          @PathVariable("id")
          @Validated
          @NotBlank
          String id) {
    log.info("Received request to retrieve brand with ID: {}", id);
    return responseBuilder.successResponse(brandService.read(id));
  }

  @PutMapping("/{id}")
  @Operation(
      summary = "Update an existing brand",
      description =
          "Updates an existing brand configuration. The brand ID in the path must match "
              + "the ID in the request body (if provided).")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Brand updated successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid request data, validation error, or ID mismatch",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing authentication token",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Brand not found",
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> update(
      @Parameter(
              description = "Unique identifier of the brand to update",
              required = true,
              example = "brn_001")
          @NotBlank
          @PathVariable
          String id,
      @Parameter(
              description =
                  "Updated brand configuration. The ID in the path will override any ID specified in the body.",
              required = true,
              content = @Content(schema = @Schema(implementation = BrandDto.class)))
          @Validated
          @NotNull
          @RequestBody
          BrandDto brandDto) {
    log.info("Received request to update brand with ID: {}", id);
    brandDto.setId(id);
    return responseBuilder.successResponse(brandService.update(brandDto));
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Delete a brand",
      description =
          "Deletes a brand configuration by its unique identifier. This operation is "
              + "irreversible.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Brand deleted successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid brand ID format",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing authentication token",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Brand not found",
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> delete(
      @Parameter(
              description = "Unique identifier of the brand to delete",
              required = true,
              example = "brn_001")
          @NotBlank
          @PathVariable("id")
          String id) {
    log.info("Received request to delete brand with ID: {}", id);
    brandService.delete(id);
    return responseBuilder.successResponse("Brand deleted successfully");
  }
}
