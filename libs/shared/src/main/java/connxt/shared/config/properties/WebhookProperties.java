package connxt.shared.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "webhook.integration")
public class WebhookProperties {

  private long defaultTimeout;
  private long connectionTimeoutMs;
  private long readTimeoutMs;
  private long maxRetryDelaySeconds;
  private double retryMultiplier;
  private int defaultRetryCount;
  private boolean enableSignatureVerification;
  private String defaultUserAgent;
  private boolean enableDetailedLogging;
  private long maxPayloadSizeBytes;
  private boolean enableHealthChecks;
  private long healthCheckTimeoutMs;
}
