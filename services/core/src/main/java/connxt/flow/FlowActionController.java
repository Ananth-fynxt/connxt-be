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

import fynxt.flowaction.dto.FlowActionDto;
import fynxt.flowaction.service.FlowActionService;
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
@RequestMapping("/flow-types/{flowTypeId}/flow-actions")
@RequiredArgsConstructor
@Validated
@Tag(
    name = "Flow Actions",
    description =
        "API endpoints for managing flow actions. Flow actions define executable operations "
            + "within a flow type, with input/output schemas and execution steps.")
public class FlowActionController {

  private final FlowActionService flowActionService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  @Operation(
      summary = "Create a new flow action",
      description =
          "Creates a new flow action for a specific flow type. Flow actions define executable "
              + "operations with input/output JSON schemas and execution steps.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow action created successfully",
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
                  "Flow action configuration details including name, steps, input schema, and output schema",
              required = true,
              content = @Content(schema = @Schema(implementation = FlowActionDto.class)))
          @Validated
          @RequestBody
          FlowActionDto dto) {
    log.info(
        "Received request to create flow action: {} for flow type: {}", dto.getName(), flowTypeId);
    dto.setFlowTypeId(flowTypeId);
    return responseBuilder.successResponse(flowActionService.create(dto));
  }

  @PutMapping("/{id}")
  @Operation(
      summary = "Update an existing flow action",
      description =
          "Updates an existing flow action configuration. The flow action ID in the path must "
              + "match the ID in the request body (if provided).")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow action updated successfully",
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
        description = "Flow action not found",
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> update(
      @Parameter(
              description = "Unique identifier of the flow action to update",
              required = true,
              example = "flow_action_001")
          @PathVariable("id")
          @NotBlank
          String id,
      @Parameter(
              description =
                  "Updated flow action configuration. The ID in the path will override any ID specified in the body.",
              required = true,
              content = @Content(schema = @Schema(implementation = FlowActionDto.class)))
          @Validated
          @RequestBody
          FlowActionDto dto) {
    log.info("Received request to update flow action with ID: {}", id);
    dto.setId(id);
    return responseBuilder.successResponse(flowActionService.update(dto));
  }

  @GetMapping
  @Operation(
      summary = "Get all flow actions for a flow type",
      description =
          "Retrieves all flow actions associated with a specific flow type. "
              + "Returns a list of flow actions that belong to the specified flow type.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow actions retrieved successfully",
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
    log.info("Received request to retrieve all flow actions for flow type: {}", flowTypeId);
    return responseBuilder.successResponse(
        flowActionService.findByFlowTypeId(flowTypeId), "Flow actions retrieved successfully");
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Get flow action by ID",
      description = "Retrieves a specific flow action configuration by its unique identifier.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow action retrieved successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid flow action ID format",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing authentication token",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Flow action not found",
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> read(
      @Parameter(description = "Unique identifier of the flow type", example = "flow_type_001")
          @PathVariable("flowTypeId")
          String flowTypeId,
      @Parameter(
              description = "Unique identifier of the flow action",
              required = true,
              example = "flow_action_001")
          @PathVariable("id")
          @NotBlank
          String id) {
    log.info("Received request to retrieve flow action with ID: {}", id);
    return responseBuilder.successResponse(flowActionService.read(id));
  }

  @GetMapping("/name/{name}")
  @Operation(
      summary = "Get flow action by name and flow type",
      description =
          "Retrieves a flow action configuration by its name and flow type ID. "
              + "Returns the flow action that matches both the name and flow type.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow action retrieved successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid flow action name or flow type ID",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing authentication token",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Flow action not found",
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> findByNameAndFlowTypeId(
      @Parameter(
              description = "Name of the flow action",
              required = true,
              example = "Process Payment")
          @PathVariable("name")
          @NotBlank
          String name,
      @Parameter(
              description = "Unique identifier of the flow type",
              required = true,
              example = "flow_type_001")
          @PathVariable("flowTypeId")
          @NotBlank
          String flowTypeId) {
    log.info(
        "Received request to retrieve flow action by name: {} for flow type: {}", name, flowTypeId);
    return responseBuilder.successResponse(
        flowActionService.findByNameAndFlowTypeId(name, flowTypeId));
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Delete a flow action",
      description =
          "Deletes a flow action configuration by its unique identifier. This operation is "
              + "irreversible.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Flow action deleted successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid flow action ID format",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Unauthorized - Invalid or missing authentication token",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Flow action not found",
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> delete(
      @Parameter(
              description = "Unique identifier of the flow action to delete",
              required = true,
              example = "flow_action_001")
          @PathVariable("id")
          @NotBlank
          String id) {
    log.info("Received request to delete flow action with ID: {}", id);
    flowActionService.delete(id);
    return responseBuilder.successResponse("Flow action deleted successfully");
  }
}
