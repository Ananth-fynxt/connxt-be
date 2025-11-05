package connxt.routingrule.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import connxt.permission.annotations.RequiresPermission;
import connxt.permission.annotations.RequiresScope;
import connxt.routingrule.dto.RoutingRuleDto;
import connxt.routingrule.dto.UpdateRoutingRuleDto;
import connxt.routingrule.service.RoutingRuleService;
import connxt.shared.builder.ResponseBuilder;
import connxt.shared.builder.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/routing-rules")
@RequiredArgsConstructor
@Validated
@RequiresScope({"SYSTEM", "FI", "BRAND"})
public class RoutingRuleController {

  private final RoutingRuleService routingRuleService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  @RequiresPermission(module = "routing_rules", action = "create")
  public ResponseEntity<ApiResponse<Object>> create(
      @Validated @RequestBody RoutingRuleDto routingRuleDto) {
    return responseBuilder.successResponse(routingRuleService.create(routingRuleDto));
  }

  @GetMapping("/{id}")
  @RequiresPermission(module = "routing_rules", action = "read")
  public ResponseEntity<ApiResponse<Object>> getById(@PathVariable String id) {
    return responseBuilder.successResponse(routingRuleService.getById(id));
  }

  @PutMapping("/{id}")
  @RequiresPermission(module = "routing_rules", action = "update")
  public ResponseEntity<ApiResponse<Object>> update(
      @PathVariable String id, @Validated @RequestBody UpdateRoutingRuleDto updateRoutingRuleDto) {
    return responseBuilder.successResponse(routingRuleService.update(id, updateRoutingRuleDto));
  }

  @DeleteMapping("/{id}")
  @RequiresPermission(module = "routing_rules", action = "delete")
  public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") String id) {
    routingRuleService.delete(id);
    return responseBuilder.successResponse("Routing rule deleted successfully");
  }

  @GetMapping("/brand/{brandId}/environment/{environmentId}")
  @RequiresPermission(module = "routing_rules", action = "read")
  public ResponseEntity<ApiResponse<Object>> readAllByBrandAndEnvironment(
      @PathVariable String brandId, @PathVariable String environmentId) {
    return responseBuilder.successResponse(
        routingRuleService.readAllByBrandAndEnvironment(brandId, environmentId));
  }
}
