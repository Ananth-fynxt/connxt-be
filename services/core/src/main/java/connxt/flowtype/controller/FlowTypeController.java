package connxt.flowtype.controller;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for managing Flow Types.
 *
 * <p>Flow Types are top-level categories for organizing flows (e.g., "Payment Processing", "Data
 * Transfer"). This controller provides CRUD operations for Flow Types using the FlowTypeService
 * from the flow library.
 */
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/flow-types")
@RequiredArgsConstructor
@Validated
public class FlowTypeController {

  private final FlowTypeService flowTypeService;
  private final ResponseBuilder responseBuilder;

  /**
   * Create a new flow type.
   *
   * @param flowTypeDto Flow type data (name is required)
   * @return Created flow type with generated ID
   */
  @PostMapping
  public ResponseEntity<ApiResponse<Object>> create(
      @Validated @RequestBody @NotNull FlowTypeDto flowTypeDto) {
    log.info("Received request to create flow type: {}", flowTypeDto.getName());
    FlowTypeDto created = flowTypeService.create(flowTypeDto);
    return responseBuilder.successResponse(created, "Flow type created successfully");
  }

  /**
   * Retrieve all flow types.
   *
   * @return List of all flow types
   */
  @GetMapping
  public ResponseEntity<ApiResponse<Object>> readAll() {
    log.info("Received request to retrieve all flow types");
    return responseBuilder.successResponse(
        flowTypeService.readAll(), "Flow types retrieved successfully");
  }

  /**
   * Retrieve a flow type by ID.
   *
   * @param id Flow type ID (e.g., "ftp_abc123xyz")
   * @return Flow type details
   */
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> read(
      @PathVariable("id") @Validated @NotBlank String id) {
    log.info("Received request to retrieve flow type with ID: {}", id);
    return responseBuilder.successResponse(flowTypeService.read(id));
  }

  /**
   * Retrieve a flow type by name.
   *
   * @param name Flow type name (e.g., "Payment Processing")
   * @return Flow type details
   */
  @GetMapping("/name/{name}")
  public ResponseEntity<ApiResponse<Object>> findByName(
      @PathVariable("name") @Validated @NotBlank String name) {
    log.info("Received request to retrieve flow type by name: {}", name);
    return responseBuilder.successResponse(flowTypeService.findByName(name));
  }

  /**
   * Update an existing flow type.
   *
   * @param id Flow type ID to update
   * @param flowTypeDto Updated flow type data
   * @return Updated flow type
   */
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> update(
      @NotBlank @PathVariable String id, @Validated @NotNull @RequestBody FlowTypeDto flowTypeDto) {
    log.info("Received request to update flow type with ID: {}", id);
    FlowTypeDto updated = flowTypeService.update(id, flowTypeDto);
    return responseBuilder.successResponse(updated, "Flow type updated successfully");
  }

  /**
   * Delete a flow type by ID.
   *
   * @param id Flow type ID to delete
   * @return Success message
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> delete(@NotBlank @PathVariable("id") String id) {
    log.info("Received request to delete flow type with ID: {}", id);
    flowTypeService.delete(id);
    return responseBuilder.successResponse("Flow type deleted successfully");
  }
}
