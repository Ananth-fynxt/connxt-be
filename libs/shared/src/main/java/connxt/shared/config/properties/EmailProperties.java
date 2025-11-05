package connxt.shared.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "email")
public class EmailProperties {

  private boolean enabled;
  private String connectionString;
  private String senderAddress;
  private boolean enableDetailedLogging;
}
