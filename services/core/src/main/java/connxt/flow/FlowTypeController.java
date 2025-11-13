package connxt.flow;

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

import connxt.shared.builder.ResponseBuilder;
import connxt.shared.builder.dto.ApiResponse;

import fynxt.flowtype.dto.FlowTypeDto;
import fynxt.flowtype.service.FlowTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/flow-types")
@RequiredArgsConstructor
@Validated
@Tag(
    name = "Flow Types",
    description =
        "API endpoints for managing flow types. Flow types categorize different transaction flows "
            + "in the system (e.g., Payment, Refund, Chargeback).")
public class FlowTypeController {

  private final FlowTypeService flowTypeService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  @Operation(
      summary = "Create a new flow type",
      description =
          "Creates a new flow type configuration. Flow types categorize different transaction flows "
              + "in the system (e.g., Payment, Refund, Chargeback).")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow type created successfully",
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
              description = "Flow type configuration details including name",
              required = true,
              content = @Content(schema = @Schema(implementation = FlowTypeDto.class)))
          @Validated
          @RequestBody
          @jakarta.validation.constraints.NotNull
          FlowTypeDto dto) {
    log.info("Received request to create flow type: {}", dto.getName());
    return responseBuilder.successResponse(flowTypeService.create(dto));
  }

  @GetMapping
  @Operation(
      summary = "Get all flow types",
      description = "Retrieves all flow type configurations in the system.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow types retrieved successfully",
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
    log.info("Received request to retrieve all flow types");
    return responseBuilder.successResponse(flowTypeService.readAll());
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Get flow type by ID",
      description = "Retrieves a specific flow type configuration by its unique identifier.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow type retrieved successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid flow type ID format",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing authentication token",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Flow type not found",
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> read(
      @Parameter(
              description = "Unique identifier of the flow type",
              required = true,
              example = "flow_type_001")
          @PathVariable("id")
          @NotBlank
          String id) {
    log.info("Received request to retrieve flow type with ID: {}", id);
    return responseBuilder.successResponse(flowTypeService.read(id));
  }

  @PutMapping("/{id}")
  @Operation(
      summary = "Update an existing flow type",
      description =
          "Updates an existing flow type configuration. The flow type ID in the path must match "
              + "the ID in the request body (if provided).")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow type updated successfully",
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
        description = "Flow type not found",
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> update(
      @Parameter(
              description = "Unique identifier of the flow type to update",
              required = true,
              example = "flow_type_001")
          @PathVariable("id")
          String id,
      @Parameter(
              description =
                  "Updated flow type configuration. The ID in the path will override any ID specified in the body.",
              required = true,
              content = @Content(schema = @Schema(implementation = FlowTypeDto.class)))
          @Validated
          @RequestBody
          FlowTypeDto dto) {
    log.info("Received request to update flow type with ID: {}", id);
    dto.setId(id);
    return responseBuilder.successResponse(flowTypeService.update(id, dto));
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Delete a flow type",
      description =
          "Deletes a flow type configuration by its unique identifier. This operation is "
              + "irreversible.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow type deleted successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid flow type ID format",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing authentication token",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Flow type not found",
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> delete(
      @Parameter(
              description = "Unique identifier of the flow type to delete",
              required = true,
              example = "flow_type_001")
          @PathVariable("id")
          @NotBlank
          String id) {
    log.info("Received request to delete flow type with ID: {}", id);
    flowTypeService.delete(id);
    return responseBuilder.successResponse("Flow type deleted successfully");
  }

  @GetMapping("/name/{name}")
  @Operation(
      summary = "Get flow type by name",
      description =
          "Retrieves a flow type configuration by its name. Returns the flow type that matches "
              + "the provided name.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow type retrieved successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid flow type name",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing authentication token",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Flow type not found",
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> findByName(
      @Parameter(description = "Name of the flow type", required = true, example = "Payment")
          @PathVariable("name")
          @NotBlank
          String name) {
    log.info("Received request to retrieve flow type by name: {}", name);
    return responseBuilder.successResponse(flowTypeService.findByName(name));
  }
}
