package nexxus.permission.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nexxus.permission.service.PermissionModuleService;
import nexxus.shared.builder.ResponseBuilder;
import nexxus.shared.builder.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

  private final PermissionModuleService permissionModuleService;
  private final ResponseBuilder responseBuilder;

  @GetMapping("/modules")
  public ResponseEntity<ApiResponse<Object>> getAvailableModules() {
    log.info("Received request to fetch available permission modules");
    return responseBuilder.successResponse(permissionModuleService.getAvailableModules());
  }
}
