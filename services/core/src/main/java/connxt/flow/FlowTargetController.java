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

import fynxt.flowtarget.dto.FlowTargetDto;
import fynxt.flowtarget.service.FlowTargetService;
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
@RequestMapping("/flow-types/{flowTypeId}/flow-targets")
@RequiredArgsConstructor
@Validated
@Tag(
    name = "Flow Targets",
    description =
        "API endpoints for managing flow targets. Flow targets represent payment gateways or "
            + "processors that can execute flow actions, with configuration for credentials, currencies, "
            + "countries, and payment methods.")
public class FlowTargetController {

  private final FlowTargetService flowTargetService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  @Operation(
      summary = "Create a new flow target",
      description =
          "Creates a new flow target for a specific flow type. Flow targets represent payment "
              + "gateways or processors with configuration for credentials, currencies, countries, "
              + "and payment methods.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow target created successfully",
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
              description = "Unique identifier of the flow type",
              required = true,
              example = "flow_type_001")
          @PathVariable("flowTypeId")
          @NotBlank
          String flowTypeId,
      @Parameter(
              description =
                  "Flow target configuration details including name, logo, credential schema, input schema, currencies, countries, and payment methods",
              required = true,
              content = @Content(schema = @Schema(implementation = FlowTargetDto.class)))
          @Validated
          @RequestBody
          FlowTargetDto dto) {
    log.info(
        "Received request to create flow target: {} for flow type: {}", dto.getName(), flowTypeId);
    dto.setFlowTypeId(flowTypeId);
    return responseBuilder.successResponse(flowTargetService.create(dto));
  }

  @GetMapping
  @Operation(
      summary = "Get all flow targets for a flow type",
      description =
          "Retrieves all flow targets associated with a specific flow type. "
              + "Returns a list of flow targets that belong to the specified flow type.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow targets retrieved successfully",
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
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> readAll(
      @Parameter(
              description = "Unique identifier of the flow type",
              required = true,
              example = "flow_type_001")
          @PathVariable("flowTypeId")
          @NotBlank
          String flowTypeId) {
    log.info("Received request to retrieve all flow targets for flow type: {}", flowTypeId);
    return responseBuilder.successResponse(flowTargetService.readAll(flowTypeId));
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Get flow target by ID",
      description = "Retrieves a specific flow target configuration by its unique identifier.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow target retrieved successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid flow target ID format",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing authentication token",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Flow target not found",
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> read(
      @Parameter(
              description = "Unique identifier of the flow target",
              required = true,
              example = "flow_target_001")
          @PathVariable("id")
          @NotBlank
          String id) {
    log.info("Received request to retrieve flow target with ID: {}", id);
    return responseBuilder.successResponse(flowTargetService.read(id));
  }

  @PutMapping("/{id}")
  @Operation(
      summary = "Update an existing flow target",
      description =
          "Updates an existing flow target configuration. The flow target ID and flow type ID in "
              + "the path must match the target being updated.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow target updated successfully",
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
        description = "Flow target not found",
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> update(
      @Parameter(
              description = "Unique identifier of the flow type",
              required = true,
              example = "flow_type_001")
          @PathVariable("flowTypeId")
          @NotBlank
          String flowTypeId,
      @Parameter(
              description = "Unique identifier of the flow target to update",
              required = true,
              example = "flow_target_001")
          @PathVariable("id")
          @NotBlank
          String id,
      @Parameter(
              description = "Updated flow target configuration",
              required = true,
              content = @Content(schema = @Schema(implementation = FlowTargetDto.class)))
          @Validated
          @RequestBody
          FlowTargetDto dto) {
    log.info(
        "Received request to update flow target with ID: {} for flow type: {}", id, flowTypeId);
    return responseBuilder.successResponse(flowTargetService.update(flowTypeId, id, dto));
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Delete a flow target",
      description =
          "Deletes a flow target configuration by its unique identifier. This operation is "
              + "irreversible.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow target deleted successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid flow target ID format",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing authentication token",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Flow target not found",
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> delete(
      @Parameter(
              description = "Unique identifier of the flow target to delete",
              required = true,
              example = "flow_target_001")
          @PathVariable("id")
          @NotBlank
          String id) {
    log.info("Received request to delete flow target with ID: {}", id);
    flowTargetService.delete(id);
    return responseBuilder.successResponse("Flow target deleted successfully");
  }
}
