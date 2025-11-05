package connxt.health.service;

import connxt.health.dto.HealthResponse;

public interface HealthService {

  HealthResponse getHealthStatus();
}
