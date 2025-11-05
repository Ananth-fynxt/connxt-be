package nexxus.flowdefinition.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import nexxus.flowdefinition.dto.FlowDefinitionDto;
import nexxus.flowdefinition.service.FlowDefinitionService;
import nexxus.shared.builder.ResponseBuilder;
import nexxus.shared.builder.dto.ApiResponse;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/flow-definitions")
@RequiredArgsConstructor
@Validated
public class FlowDefinitionController {

  private final FlowDefinitionService flowDefinitionService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  public ResponseEntity<ApiResponse<Object>> create(@Validated @RequestBody FlowDefinitionDto dto) {
    return responseBuilder.successResponse(flowDefinitionService.create(dto));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<Object>> readAll() {
    return responseBuilder.successResponse(flowDefinitionService.readAll());
  }

  @GetMapping("/flow-target/{flowTargetId}")
  public ResponseEntity<ApiResponse<Object>> readAllByFlowTargetId(
      @PathVariable("flowTargetId") @NotBlank String flowTargetId) {
    return responseBuilder.successResponse(
        flowDefinitionService.readAllByFlowTargetId(flowTargetId));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> read(@PathVariable("id") @NotBlank String id) {
    return responseBuilder.successResponse(flowDefinitionService.read(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> update(
      @PathVariable("id") String id, @Validated @RequestBody FlowDefinitionDto dto) {
    dto.setId(id);
    return responseBuilder.successResponse(flowDefinitionService.update(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") @NotBlank String id) {
    flowDefinitionService.delete(id);
    return responseBuilder.successResponse("Flow definition deleted successfully");
  }

  @GetMapping("/brand")
  public ResponseEntity<ApiResponse<Object>> readAllByBrand() {
    return responseBuilder.successResponse(flowDefinitionService.readAllByBrand());
  }
}
