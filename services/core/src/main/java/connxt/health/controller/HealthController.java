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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@Validated
@Tag(
    name = "Health",
    description =
        "API endpoints for health checks. Provides system health status information for "
            + "monitoring and load balancer health checks.")
public class HealthController {

  private final HealthService healthService;
  private final ResponseBuilder responseBuilder;

  @GetMapping
  @Operation(
      summary = "Health check (GET)",
      description =
          "Retrieves the current health status of the system. This endpoint is typically used "
              + "by load balancers and monitoring systems to verify system availability.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Health status retrieved successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<Object>> health() {
    log.info("Received request for health check GET");
    return responseBuilder.successResponse(healthService.getHealthStatus());
  }

  @PostMapping
  @Operation(
      summary = "Health check (POST)",
      description =
          "Retrieves the current health status of the system via POST request. This endpoint "
              + "provides the same functionality as the GET endpoint but uses POST method.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Health status retrieved successfully",
        content =
            @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)))
  })
  public ResponseEntity<ApiResponse<Object>> healthPost() {
    log.info("Received request for health check POST");
    return responseBuilder.successResponse(healthService.getHealthStatus());
  }
}
