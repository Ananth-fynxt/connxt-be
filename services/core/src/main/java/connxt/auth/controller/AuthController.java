package connxt.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import connxt.auth.dto.AuthResponse;
import connxt.auth.dto.LoginRequest;
import connxt.auth.service.AuthService;
import connxt.shared.builder.ResponseBuilder;
import connxt.shared.builder.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
@Tag(
    name = "Authentication",
    description =
        "API endpoints for user authentication. Provides login, token refresh, and logout "
            + "functionality for system users.")
public class AuthController {

  private final AuthService authService;
  private final ResponseBuilder responseBuilder;

  @PostMapping("/login")
  @Operation(
      summary = "User login",
      description =
          "Authenticates a user with email and password. Returns an authentication response "
              + "containing access token and refresh token.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Login successful",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid request data or validation error",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Invalid credentials",
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> login(
      @Parameter(
              description = "Login credentials including email and password",
              required = true,
              content = @Content(schema = @Schema(implementation = LoginRequest.class)))
          @Validated
          @RequestBody
          LoginRequest request) {
    log.info("Login request received for email: {}", request.getEmail());
    AuthResponse response = authService.login(request);
    return responseBuilder.successResponse(response);
  }

  @PostMapping("/token/refresh")
  @Operation(
      summary = "Refresh access token",
      description =
          "Refreshes an expired access token using a valid refresh token. Returns a new "
              + "authentication response with updated tokens.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Token refreshed successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid refresh token format",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Invalid or expired refresh token",
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> refreshToken(
      @Parameter(
              description = "Refresh token to use for generating a new access token",
              required = true,
              example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
          @RequestParam
          String refreshToken) {
    log.info("Token refresh request received");
    AuthResponse response = authService.refreshToken(refreshToken);
    return responseBuilder.successResponse(response);
  }

  @PostMapping("/logout")
  @Operation(
      summary = "User logout",
      description =
          "Logs out a user by invalidating their access token. The token provided in the "
              + "Authorization header will be revoked.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Logout successful",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid authorization header format",
        content = @Content(mediaType = "application/json")),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "Invalid or missing authorization token",
        content = @Content(mediaType = "application/json"))
  })
  public ResponseEntity<ApiResponse<Object>> logout(
      @Parameter(
              description = "Authorization header containing Bearer token",
              required = true,
              example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
          @RequestHeader("Authorization")
          String authorization) {
    log.info("Logout request received");
    return responseBuilder.successResponse(authService.logout(authorization));
  }
}
