package nexxus.request.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import nexxus.permission.annotations.RequiresScope;
import nexxus.request.dto.RequestInputDto;
import nexxus.request.service.RequestService;
import nexxus.shared.builder.ResponseBuilder;
import nexxus.shared.builder.dto.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
@RequiresScope({"EXTERNAL"})
public class RequestController {

  private final RequestService requestService;
  private final ResponseBuilder responseBuilder;

  @PostMapping("/fetch-psp")
  public ResponseEntity<ApiResponse<Object>> fetchPsp(
      @Validated @RequestBody RequestInputDto requestInputDto, HttpServletRequest httpRequest) {
    log.info(
        "Received request to fetch PSPs for brand: {} and environment: {}",
        requestInputDto.getBrandId(),
        requestInputDto.getEnvironmentId());

    // Extract client IP address from request
    requestInputDto.setClientIpAddress(getClientIpAddress(httpRequest));

    return responseBuilder.successResponse(requestService.fetchPsp(requestInputDto));
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
