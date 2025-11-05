package nexxus.shared.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import nexxus.shared.config.properties.WebhookProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration class for webhook integration library. This provides the necessary beans for
 * webhook execution functionality.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(WebhookProperties.class)
public class WebhookConfig {

  /** Configure RestTemplate for webhook HTTP requests using centralized configuration */
  @Bean("webhookRestTemplate")
  public RestTemplate webhookRestTemplate(@Qualifier("restTemplate") RestTemplate restTemplate) {
    log.info("Using centralized RestTemplate for webhook HTTP requests");
    return restTemplate;
  }

  /** Configure ObjectMapper for JSON serialization/deserialization */
  @Bean
  public ObjectMapper webhookObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    log.info("Configured webhook ObjectMapper with JavaTimeModule");

    return mapper;
  }
}
