package nexxus.flowtarget.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import nexxus.flowtarget.dto.FlowTargetDto;
import nexxus.flowtarget.service.FlowTargetService;
import nexxus.shared.builder.ResponseBuilder;
import nexxus.shared.builder.dto.ApiResponse;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/flow-types/{flowTypeId}/flow-targets")
@RequiredArgsConstructor
@Validated
public class FlowTargetController {

  private final FlowTargetService flowTargetService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  public ResponseEntity<ApiResponse<Object>> create(
      @PathVariable("flowTypeId") @NotBlank String flowTypeId,
      @Validated @RequestBody FlowTargetDto dto) {
    dto.setFlowTypeId(flowTypeId);
    return responseBuilder.successResponse(flowTargetService.create(dto));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<Object>> readAll(
      @PathVariable("flowTypeId") @NotBlank String flowTypeId) {
    return responseBuilder.successResponse(flowTargetService.readAll(flowTypeId));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> read(@PathVariable("id") @NotBlank String id) {
    return responseBuilder.successResponse(flowTargetService.read(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> update(
      @PathVariable("flowTypeId") @NotBlank String flowTypeId,
      @PathVariable("id") @NotBlank String id,
      @Validated @RequestBody FlowTargetDto dto) {
    return responseBuilder.successResponse(flowTargetService.update(flowTypeId, id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") @NotBlank String id) {
    flowTargetService.delete(id);
    return responseBuilder.successResponse("Flow target deleted successfully");
  }
}
