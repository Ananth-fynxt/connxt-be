package nexxus.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import nexxus.auth.dto.AuthResponse;
import nexxus.auth.dto.ExternalLoginRequest;
import nexxus.auth.dto.LoginRequest;
import nexxus.auth.service.AuthService;
import nexxus.shared.builder.ResponseBuilder;
import nexxus.shared.builder.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

  private final AuthService authService;
  private final ResponseBuilder responseBuilder;

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<Object>> login(@Validated @RequestBody LoginRequest request) {
    log.info("Login request received for email: {}", request.getEmail());
    AuthResponse response = authService.login(request);
    return responseBuilder.successResponse(response);
  }

  @PostMapping("/external/login")
  public ResponseEntity<ApiResponse<Object>> externalLogin(
      @Validated @RequestBody ExternalLoginRequest request) {
    log.info("External login request received for customer: {}", request.getCustomerId());
    AuthResponse response = authService.externalLogin(request);
    return responseBuilder.successResponse(response);
  }

  @PostMapping("/token/refresh")
  public ResponseEntity<ApiResponse<Object>> refreshToken(@RequestParam String refreshToken) {
    log.info("Token refresh request received");
    AuthResponse response = authService.refreshToken(refreshToken);
    return responseBuilder.successResponse(response);
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Object>> logout(
      @RequestHeader("Authorization") String authorization) {
    log.info("Logout request received");
    return responseBuilder.successResponse(authService.logout(authorization));
  }
}
