package nexxus.health.service.impl;

import org.springframework.stereotype.Service;

import nexxus.health.dto.HealthResponse;
import nexxus.health.service.HealthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthServiceImpl implements HealthService {

  @Override
  public HealthResponse getHealthStatus() {
    log.debug("Generating health status response");
    return HealthResponse.healthy();
  }
}
