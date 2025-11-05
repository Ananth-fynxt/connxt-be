package nexxus.flowtype.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import nexxus.flowtype.dto.FlowTypeDto;
import nexxus.flowtype.service.FlowTypeService;
import nexxus.shared.builder.ResponseBuilder;
import nexxus.shared.builder.dto.ApiResponse;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/flow-types")
@RequiredArgsConstructor
@Validated
public class FlowTypeController {

  private final FlowTypeService flowTypeService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  public ResponseEntity<ApiResponse<Object>> create(@Validated @RequestBody FlowTypeDto dto) {
    return responseBuilder.successResponse(flowTypeService.create(dto));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<Object>> readAll() {
    return responseBuilder.successResponse(flowTypeService.readAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> read(@PathVariable("id") @NotBlank String id) {
    return responseBuilder.successResponse(flowTypeService.read(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> update(
      @PathVariable("id") String id, @Validated @RequestBody FlowTypeDto dto) {
    dto.setId(id);
    return responseBuilder.successResponse(flowTypeService.update(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") @NotBlank String id) {
    flowTypeService.delete(id);
    return responseBuilder.successResponse("Flow type deleted successfully");
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<ApiResponse<Object>> findByName(
      @PathVariable("name") @NotBlank String name) {
    return responseBuilder.successResponse(flowTypeService.findByName(name));
  }
}
