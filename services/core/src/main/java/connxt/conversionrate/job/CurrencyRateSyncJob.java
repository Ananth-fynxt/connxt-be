package connxt.conversionrate.job;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import connxt.conversionrate.dto.ConversionRateRawDataDto;
import connxt.conversionrate.dto.FixerApiResponseDto;
import connxt.conversionrate.entity.FixerApiCurrencyPair;
import connxt.conversionrate.service.ConversionRateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyRateSyncJob {

  private final ConversionRateService conversionRateService;
  private final JobScheduler jobScheduler;
  private final RestTemplate restTemplate;

  @Value("${fixer.api.base-url}")
  private String fixerBaseUrl;

  @Value("${fixer.api.access-key}")
  private String fixerAccessKey;

  @Value("${fixer.api.schedule}")
  private String fixerSchedule;

  @Value("${fixer.api.job-name}")
  private String fixerJobName;

  @Value("${fixer.api.description}")
  private String fixerDescription;

  @Job(name = "Currency Rate Sync Job", retries = 3)
  public void executeCurrencyRateSync() {
    log.info("Starting {} - {}", fixerJobName, fixerDescription);

    try {
      if (fixerAccessKey == null || fixerAccessKey.trim().isEmpty()) {
        throw new IllegalStateException("Fixer API access key is not configured");
      }

      List<FixerApiCurrencyPair> currencyPairs = conversionRateService.getAllCurrencyPairs();

      if (currencyPairs.isEmpty()) {
        log.warn(
            "No currency pairs found in database. Please add currency pairs to fixer_api_currency_pairs table.");
        return;
      }

      log.info("Found {} currency pairs in database", currencyPairs.size());

      int totalStoredCount = 0;

      for (FixerApiCurrencyPair currencyPair : currencyPairs) {
        try {
          LocalDate yesterday = LocalDate.now().minusDays(1);
          String dateStr = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

          log.info(
              "Processing currency pair: {} -> {} for date: {}",
              currencyPair.getSourceCurrency(),
              currencyPair.getTargetCurrency(),
              dateStr);

          String symbols = String.join(",", currencyPair.getTargetCurrency());

          String url =
              String.format(
                  "%s/%s?base=%s&symbols=%s&access_key=%s",
                  fixerBaseUrl, dateStr, currencyPair.getSourceCurrency(), symbols, fixerAccessKey);

          log.info("Calling Fixer.io API: {}", url.replace(fixerAccessKey, "***"));

          FixerApiResponseDto response = restTemplate.getForObject(url, FixerApiResponseDto.class);

          if (response == null) {
            log.error(
                "No response received from Fixer.io API for {} -> {}",
                currencyPair.getSourceCurrency(),
                symbols);
            continue;
          }

          if (!response.isSuccess()) {
            log.error(
                "Fixer.io API returned unsuccessful response for {} -> {}",
                currencyPair.getSourceCurrency(),
                symbols);
            continue;
          }

          if (response.getRates() == null || response.getRates().isEmpty()) {
            log.warn(
                "Fixer.io API returned empty rates for {} -> {}",
                currencyPair.getSourceCurrency(),
                symbols);
            continue;
          }

          log.info(
              "Got response from Fixer.io with {} rates for base currency: {}",
              response.getRates().size(),
              response.getBase());

          LocalDateTime now = LocalDateTime.now();
          int storedCount = 0;

          for (Map.Entry<String, BigDecimal> rate : response.getRates().entrySet()) {
            try {
              ConversionRateRawDataDto rawData =
                  ConversionRateRawDataDto.builder()
                      .sourceCurrency(response.getBase())
                      .targetCurrency(rate.getKey())
                      .timeRange(now)
                      .amount(rate.getValue())
                      .build();

              conversionRateService.upsertRawData(rawData);
              storedCount++;
              totalStoredCount++;

              log.info(
                  "Stored rate: {} -> {} = {}", response.getBase(), rate.getKey(), rate.getValue());

            } catch (Exception e) {
              log.error(
                  "Failed to store rate for {} -> {}: {}",
                  response.getBase(),
                  rate.getKey(),
                  e.getMessage());
            }
          }

          log.info(
              "Successfully stored {}/{} rates for {} -> {}",
              storedCount,
              response.getRates().size(),
              currencyPair.getSourceCurrency(),
              symbols);

        } catch (Exception e) {
          log.error(
              "Error processing currency pair {} -> {}: {}",
              currencyPair.getSourceCurrency(),
              currencyPair.getTargetCurrency(),
              e.getMessage());
        }
      }

      log.info(
          "Successfully processed {} currency pairs and stored {} total rates",
          currencyPairs.size(),
          totalStoredCount);

    } catch (Exception e) {
      log.error("Error during currency rate sync job", e);
      throw new RuntimeException("Currency rate sync job failed: " + e.getMessage(), e);
    }
  }

  public void scheduleRecurringJob() {
    log.info("Scheduling {} with schedule: {}", fixerJobName, fixerSchedule);

    jobScheduler.scheduleRecurrently(fixerJobName, fixerSchedule, () -> executeCurrencyRateSync());

    log.info("Successfully scheduled {} with schedule: {}", fixerJobName, fixerSchedule);
  }
}
