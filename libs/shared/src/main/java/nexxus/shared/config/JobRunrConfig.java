package nexxus.shared.config;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.configuration.JobRunrConfiguration;
import org.jobrunr.configuration.JobRunrConfiguration.JobRunrConfigurationResult;
import org.jobrunr.storage.StorageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class JobRunrConfig {

  @Autowired(required = false)
  private StorageProvider storageProvider;

  @Autowired private ApplicationContext applicationContext;

  @Bean
  @ConditionalOnProperty(
      name = "jobrunr.background-job-server.enabled",
      havingValue = "true",
      matchIfMissing = false)
  public JobRunrConfigurationResult jobRunrConfiguration() {
    log.info("Configuring JobRunr explicitly with background job server enabled");

    if (storageProvider == null) {
      log.error("StorageProvider is null - JobRunr cannot be configured");
      return null;
    }

    JobRunrConfiguration.JobRunrConfigurationResult result =
        JobRunr.configure()
            .useStorageProvider(storageProvider)
            .useJobActivator(applicationContext::getBean)
            .useBackgroundJobServer()
            .useDashboard()
            .initialize();

    log.info("JobRunr configured successfully: {}", result);
    return result;
  }
}
