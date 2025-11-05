package nexxus.conversionrate.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import nexxus.conversionrate.job.CurrencyRateSyncJob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyRateJobConfig implements ApplicationListener<ContextRefreshedEvent> {

  private final CurrencyRateSyncJob currencyRateSyncJob;

  @Override
  public void onApplicationEvent(@org.springframework.lang.NonNull ContextRefreshedEvent event) {
    // Only initialize on the root application context
    if (event.getApplicationContext().getParent() == null) {
      log.info("Starting currency rate sync job scheduler");
      currencyRateSyncJob.scheduleRecurringJob();
    }
  }
}
