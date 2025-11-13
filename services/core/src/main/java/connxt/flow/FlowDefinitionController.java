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

import fynxt.flowdefinition.dto.FlowDefinitionDto;
import fynxt.flowdefinition.service.FlowDefinitionService;
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
@RequestMapping("/flow-definitions")
@RequiredArgsConstructor
@Validated
@Tag(
    name = "Flow Definitions",
    description =
        "API endpoints for managing flow definitions. Flow definitions link flow actions with "
            + "flow targets, defining how specific actions are executed on specific targets.")
public class FlowDefinitionController {

  private final FlowDefinitionService flowDefinitionService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  @Operation(
      summary = "Create a new flow definition",
      description =
          "Creates a new flow definition that links a flow action with a flow target. "
              + "Flow definitions specify how actions are executed on specific targets.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow definition created successfully",
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
                  "Flow definition configuration details including flow action ID, flow target ID, code, description, and optional brand ID",
              required = true,
              content = @Content(schema = @Schema(implementation = FlowDefinitionDto.class)))
          @Validated
          @RequestBody
          FlowDefinitionDto dto) {
    log.info("Received request to create flow definition with code: {}", dto.getCode());
    return responseBuilder.successResponse(flowDefinitionService.create(dto));
  }

  @GetMapping
  @Operation(
      summary = "Get all flow definitions",
      description = "Retrieves all flow definition configurations in the system.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow definitions retrieved successfully",
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
    log.info("Received request to retrieve all flow definitions");
    return responseBuilder.successResponse(flowDefinitionService.readAll());
  }

  @GetMapping("/flow-target/{flowTargetId}")
  @Operation(
      summary = "Get all flow definitions by flow target ID",
      description =
          "Retrieves all flow definitions associated with a specific flow target. "
              + "Returns a list of flow definitions that use the specified flow target.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow definitions retrieved successfully",
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
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> readAllByFlowTargetId(
      @Parameter(
              description = "Unique identifier of the flow target",
              required = true,
              example = "flow_target_001")
          @PathVariable("flowTargetId")
          @NotBlank
          String flowTargetId) {
    log.info("Received request to retrieve flow definitions for flow target ID: {}", flowTargetId);
    return responseBuilder.successResponse(
        flowDefinitionService.readAllByFlowTargetId(flowTargetId));
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Get flow definition by ID",
      description = "Retrieves a specific flow definition configuration by its unique identifier.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow definition retrieved successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid flow definition ID format",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing authentication token",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Flow definition not found",
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> read(
      @Parameter(
              description = "Unique identifier of the flow definition",
              required = true,
              example = "flow_def_001")
          @PathVariable("id")
          @NotBlank
          String id) {
    log.info("Received request to retrieve flow definition with ID: {}", id);
    return responseBuilder.successResponse(flowDefinitionService.read(id));
  }

  @PutMapping("/{id}")
  @Operation(
      summary = "Update an existing flow definition",
      description =
          "Updates an existing flow definition configuration. The flow definition ID in the path "
              + "must match the ID in the request body (if provided).")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow definition updated successfully",
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
        description = "Flow definition not found",
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> update(
      @Parameter(
              description = "Unique identifier of the flow definition to update",
              required = true,
              example = "flow_def_001")
          @PathVariable("id")
          String id,
      @Parameter(
              description =
                  "Updated flow definition configuration. The ID in the path will override any ID specified in the body.",
              required = true,
              content = @Content(schema = @Schema(implementation = FlowDefinitionDto.class)))
          @Validated
          @RequestBody
          FlowDefinitionDto dto) {
    log.info("Received request to update flow definition with ID: {}", id);
    dto.setId(id);
    return responseBuilder.successResponse(flowDefinitionService.update(id, dto));
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Delete a flow definition",
      description =
          "Deletes a flow definition configuration by its unique identifier. This operation is "
              + "irreversible.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow definition deleted successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid flow definition ID format",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing authentication token",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Flow definition not found",
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> delete(
      @Parameter(
              description = "Unique identifier of the flow definition to delete",
              required = true,
              example = "flow_def_001")
          @PathVariable("id")
          @NotBlank
          String id) {
    log.info("Received request to delete flow definition with ID: {}", id);
    flowDefinitionService.delete(id);
    return responseBuilder.successResponse("Flow definition deleted successfully");
  }
}
