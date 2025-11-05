package nexxus.session.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import nexxus.permission.annotations.RequiresScope;
import nexxus.session.dto.SessionDto;
import nexxus.session.service.WidgetSessionService;
import nexxus.shared.builder.ResponseBuilder;
import nexxus.shared.builder.dto.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
@Validated
@RequiresScope({"EXTERNAL"})
public class WidgetSessionController {

  private final WidgetSessionService widgetSessionService;
  private final ResponseBuilder responseBuilder;

  @PostMapping
  public ResponseEntity<ApiResponse<Object>> create(
      @Valid @RequestBody SessionDto.CreateRequest requestDto, HttpServletRequest httpRequest) {
    log.info(
        "Received request to create session for customer: {}, brand: {}, environment: {}",
        requestDto.getCustomerId(),
        requestDto.getBrandId(),
        requestDto.getEnvironmentId());

    // Enhance device info with request details if not provided
    if (requestDto.getDeviceInfo() == null) {
      requestDto.setDeviceInfo(SessionDto.DeviceInfo.builder().build());
    }

    // Set IP address in fingerprint if not already set
    if (requestDto.getDeviceInfo().getFingerprint() != null
        && requestDto.getDeviceInfo().getFingerprint().getIpAddress() == null) {
      requestDto.getDeviceInfo().getFingerprint().setIpAddress(getClientIpAddress(httpRequest));
    }

    return responseBuilder.successResponse(widgetSessionService.create(requestDto));
  }

  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<Object>> refresh(
      @Valid @RequestBody SessionDto.RefreshRequest requestDto) {
    log.info("Received request to refresh session");
    return responseBuilder.successResponse(
        widgetSessionService.refresh(requestDto.getSessionToken(), requestDto.getFingerprint()));
  }

  @PostMapping("/validate")
  public ResponseEntity<ApiResponse<Object>> validate(
      @Valid @RequestBody SessionDto.ValidateRequest requestDto) {
    log.info("Received request to validate session");
    return responseBuilder.successResponse(
        widgetSessionService.validate(requestDto.getSessionToken(), requestDto.getFingerprint()));
  }

  @PostMapping("/revoke")
  public ResponseEntity<ApiResponse<Object>> revoke(
      @Valid @RequestBody SessionDto.RevokeRequest requestDto) {
    log.info("Received request to revoke session: {}", requestDto.getSessionId());
    widgetSessionService.revoke(requestDto);
    return responseBuilder.successResponse("Session revoked successfully");
  }

  @PostMapping("/revoke-all")
  public ResponseEntity<ApiResponse<Object>> revokeAll(
      @RequestParam @NotBlank String customerId,
      @RequestParam @NotBlank String brandId,
      @RequestParam @NotBlank String environmentId) {
    log.info(
        "Received request to revoke all sessions for customer: {}, brand: {}, environment: {}",
        customerId,
        brandId,
        environmentId);
    widgetSessionService.revokeAll(customerId, brandId, environmentId);
    return responseBuilder.successResponse("All sessions revoked successfully");
  }

  private String getClientIpAddress(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }
    String xRealIp = request.getHeader("X-Real-IP");
    if (xRealIp != null && !xRealIp.isEmpty()) {
      return xRealIp;
    }
    return request.getRemoteAddr();
  }
}
