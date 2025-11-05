package connxt.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RestTemplateConfig {

  @Value("${http.client.connection-timeout-ms:10000}")
  private long connectionTimeoutMs;

  @Value("${http.client.read-timeout-ms:30000}")
  private long readTimeoutMs;

  @Bean
  public RestTemplate restTemplate() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout((int) connectionTimeoutMs);
    factory.setReadTimeout((int) readTimeoutMs);

    RestTemplate restTemplate = new RestTemplate(factory);

    log.info(
        "Configured RestTemplate with connection timeout: {}ms, read timeout: {}ms",
        connectionTimeoutMs,
        readTimeoutMs);

    return restTemplate;
  }
}
