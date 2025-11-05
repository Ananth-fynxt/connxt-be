package nexxus.systemuser.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import nexxus.permission.annotations.RequiresScope;
import nexxus.shared.builder.ResponseBuilder;
import nexxus.shared.builder.dto.ApiResponse;
import nexxus.systemuser.dto.SystemUserDto;
import nexxus.systemuser.service.SystemUserService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/system-users")
@RequiredArgsConstructor
@Validated
@RequiresScope({"SYSTEM"})
public class SystemUserController {

  private final SystemUserService systemUserService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  public ResponseEntity<ApiResponse<Object>> create(
      @Validated @RequestBody @NotNull SystemUserDto dto) {
    log.info("Received request to create system user: {}", dto.getName());
    return responseBuilder.successResponse(systemUserService.createSystemUser(dto));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<Object>> readAll() {
    log.info("Received request to retrieve all system users");
    return responseBuilder.successResponse(
        systemUserService.getAllSystemUsers(), "System users retrieved successfully");
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> read(
      @PathVariable("id") @Validated @NotBlank String id) {
    log.info("Received request to retrieve system user with ID: {}", id);
    return responseBuilder.successResponse(systemUserService.getSystemUserById(id));
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<ApiResponse<Object>> readByEmail(
      @PathVariable("email") @Validated @NotBlank String email) {
    log.info("Received request to retrieve system user with email: {}", email);
    return responseBuilder.successResponse(systemUserService.getSystemUserByEmail(email));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> update(
      @NotBlank @PathVariable String id,
      @Validated @NotNull @RequestBody SystemUserDto systemUserDto) {
    log.info("Received request to update system user with ID: {}", id);
    systemUserDto.setId(id);
    return responseBuilder.successResponse(systemUserService.updateSystemUser(id, systemUserDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Object>> delete(@NotBlank @PathVariable("id") String id) {
    log.info("Received request to delete system user with ID: {}", id);
    systemUserService.deleteSystemUser(id);
    return responseBuilder.successResponse("System user deleted successfully");
  }
}
