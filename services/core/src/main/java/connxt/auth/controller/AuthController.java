package connxt.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import connxt.auth.dto.AuthResponse;
import connxt.auth.dto.LoginRequest;
import connxt.auth.service.AuthService;
import connxt.shared.builder.ResponseBuilder;
import connxt.shared.builder.dto.ApiResponse;

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
