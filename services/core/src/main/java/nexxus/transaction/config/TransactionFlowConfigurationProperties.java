package nexxus.transaction.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
@ConfigurationProperties(prefix = "transaction.flow-configuration.cache")
public class TransactionFlowConfigurationProperties {

  private int maximumSize = 5000;
  private Duration expireAfterWrite = Duration.ofMinutes(5);
}
