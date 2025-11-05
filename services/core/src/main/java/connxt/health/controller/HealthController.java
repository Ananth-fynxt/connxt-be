package connxt.health.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import connxt.health.service.HealthService;
import connxt.shared.builder.ResponseBuilder;
import connxt.shared.builder.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@Validated
public class HealthController {

  private final HealthService healthService;
  private final ResponseBuilder responseBuilder;

  @GetMapping
  public ResponseEntity<ApiResponse<Object>> health() {
    log.info("Received request for health check GET");
    return responseBuilder.successResponse(healthService.getHealthStatus());
  }

  @PostMapping
  public ResponseEntity<ApiResponse<Object>> healthPost() {
    log.info("Received request for health check POST");
    return responseBuilder.successResponse(healthService.getHealthStatus());
  }
}
