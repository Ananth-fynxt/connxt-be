package connxt.flowtype.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import connxt.flowtype.dto.FlowTypeDto;
import connxt.flowtype.service.FlowTypeService;
import connxt.shared.builder.ResponseBuilder;
import connxt.shared.builder.dto.ApiResponse;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@PreAuthorize("hasRole('ADMIN')")
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
