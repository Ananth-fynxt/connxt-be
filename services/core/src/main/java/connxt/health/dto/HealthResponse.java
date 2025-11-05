package connxt.health.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Health check response")
public class HealthResponse {

  @Schema(description = "Health status")
  private String status;

  @Schema(description = "Timestamp of the health check")
  @JsonProperty("timestamp")
  private String timestamp;

  @Schema(description = "Service name")
  private String service;

  public static HealthResponse healthy() {
    return HealthResponse.builder()
        .status("healthy")
        .timestamp(Instant.now().toString())
        .service("Backend Service - Connxt")
        .build();
  }
}
