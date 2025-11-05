package nexxus.shared.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.azure.communication.email.EmailAsyncClient;
import com.azure.communication.email.EmailClientBuilder;

import nexxus.shared.config.properties.EmailProperties;

import lombok.extern.slf4j.Slf4j;

/**
 * Configuration class for email service settings. Configures Azure Communication Services email
 * client and enables email properties.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(EmailProperties.class)
public class EmailConfig {

  @Bean
  @Primary
  @ConditionalOnProperty(name = "email.enabled", havingValue = "true", matchIfMissing = true)
  public EmailAsyncClient emailClient(EmailProperties emailProperties) {
    log.info("Configuring Azure Communication Services Email Client");

    if (emailProperties.getConnectionString() == null
        || emailProperties.getConnectionString().isEmpty()) {
      log.error("Email connection string is not configured - Email service will not be available");
      return null;
    }

    try {
      EmailAsyncClient emailClient =
          new EmailClientBuilder()
              .connectionString(emailProperties.getConnectionString())
              .buildAsyncClient();

      log.info("Email client configured successfully");
      return emailClient;
    } catch (Exception e) {
      log.error("Failed to configure email client: {}", e.getMessage(), e);
      return null;
    }
  }
}
