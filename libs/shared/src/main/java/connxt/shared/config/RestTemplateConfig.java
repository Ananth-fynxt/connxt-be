package connxt.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import connxt.shared.config.properties.WebhookProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RestTemplateConfig {

  @Bean
  public RestTemplate restTemplate(WebhookProperties properties) {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout((int) properties.getConnectionTimeoutMs());
    factory.setReadTimeout((int) properties.getReadTimeoutMs());

    RestTemplate restTemplate = new RestTemplate(factory);

    log.info(
        "Configured webhook RestTemplate with connection timeout: {}ms, read timeout: {}ms",
        properties.getConnectionTimeoutMs(),
        properties.getReadTimeoutMs());

    return restTemplate;
  }
}
