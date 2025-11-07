package connxt.systemrole.controller;

import org.springframework.http.ResponseEntity;
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
import connxt.systemrole.dto.SystemRoleDto;
import connxt.systemrole.service.SystemRoleService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/system-roles")
@RequiredArgsConstructor
@Validated
public class SystemRoleController {

  private final SystemRoleService systemRoleService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  public ResponseEntity<ApiResponse<Object>> create(
      @Validated @RequestBody @NotNull SystemRoleDto systemRoleDto) {
    log.info("Received request to create system role: {}", systemRoleDto.getName());
    return responseBuilder.successResponse(systemRoleService.create(systemRoleDto));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<Object>> readAll() {
    log.info("Received request to retrieve all system roles");
    return responseBuilder.successResponse(
        systemRoleService.readAll(), "System roles retrieved successfully");
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> read(@PathVariable("id") @NotBlank String id) {
    log.info("Received request to retrieve system role with ID: {}", id);
    return responseBuilder.successResponse(systemRoleService.read(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> update(
      @NotBlank @PathVariable String id,
      @Validated @NotNull @RequestBody SystemRoleDto systemRoleDto) {
    log.info("Received request to update system role with ID: {}", id);
    systemRoleDto.setId(id);
    return responseBuilder.successResponse(systemRoleService.update(systemRoleDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> delete(@NotBlank @PathVariable("id") String id) {
    log.info("Received request to delete system role with ID: {}", id);
    systemRoleService.delete(id);
    return responseBuilder.successResponse("System role deleted successfully");
  }
}
