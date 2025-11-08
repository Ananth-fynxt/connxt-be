package connxt.flowaction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import connxt.flowaction.dto.FlowActionDto;
import connxt.flowaction.service.FlowActionService;
import connxt.shared.builder.ResponseBuilder;
import connxt.shared.builder.dto.ApiResponse;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/flow-types/{flowTypeId}/flow-actions")
@RequiredArgsConstructor
@Validated
public class FlowActionController {

  private final FlowActionService flowActionService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  public ResponseEntity<ApiResponse<Object>> create(
      @PathVariable("flowTypeId") @NotBlank String flowTypeId,
      @Validated @RequestBody FlowActionDto dto) {
    log.info(
        "Received request to create flow action: {} for flow type: {}", dto.getName(), flowTypeId);
    dto.setFlowTypeId(flowTypeId);
    return responseBuilder.successResponse(flowActionService.create(dto));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> update(
      @PathVariable("id") @NotBlank String id, @Validated @RequestBody FlowActionDto dto) {
    log.info("Received request to update flow action with ID: {}", id);
    dto.setId(id);
    return responseBuilder.successResponse(flowActionService.update(dto));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<Object>> readAll(
      @PathVariable("flowTypeId") @NotBlank String flowTypeId) {
    log.info("Received request to retrieve all flow actions for flow type: {}", flowTypeId);
    return responseBuilder.successResponse(
        flowActionService.findByFlowTypeId(flowTypeId), "Flow actions retrieved successfully");
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> read(
      @PathVariable("flowTypeId") String flowTypeId, @PathVariable("id") @NotBlank String id) {
    log.info("Received request to retrieve flow action with ID: {}", id);
    return responseBuilder.successResponse(flowActionService.read(id));
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<ApiResponse<Object>> findByNameAndFlowTypeId(
      @PathVariable("name") @NotBlank String name,
      @PathVariable("flowTypeId") @NotBlank String flowTypeId) {
    log.info(
        "Received request to retrieve flow action by name: {} for flow type: {}", name, flowTypeId);
    return responseBuilder.successResponse(
        flowActionService.findByNameAndFlowTypeId(name, flowTypeId));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") @NotBlank String id) {
    log.info("Received request to delete flow action with ID: {}", id);
    flowActionService.delete(id);
    return responseBuilder.successResponse("Flow action deleted successfully");
  }
}
