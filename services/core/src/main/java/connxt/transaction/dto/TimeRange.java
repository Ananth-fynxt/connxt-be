package connxt.transaction.dto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Enum representing predefined time ranges for filtering transactions. Each enum value provides a
 * method to calculate the start date relative to the current time.
 */
public enum TimeRange {
  LAST_24_HOURS(1, ChronoUnit.DAYS),
  LAST_2_DAYS(2, ChronoUnit.DAYS),
  LAST_3_DAYS(3, ChronoUnit.DAYS),
  LAST_4_DAYS(4, ChronoUnit.DAYS),
  LAST_7_DAYS(7, ChronoUnit.DAYS),
  LAST_30_DAYS(30, ChronoUnit.DAYS),
  LAST_3_MONTHS(3, ChronoUnit.MONTHS),
  LAST_6_MONTHS(6, ChronoUnit.MONTHS),
  LAST_YEAR(1, ChronoUnit.YEARS);

  private final long amount;
  private final ChronoUnit unit;

  TimeRange(long amount, ChronoUnit unit) {
    this.amount = amount;
    this.unit = unit;
  }

  /**
   * Calculates the start date for this time range relative to the current time.
   *
   * @return LocalDateTime representing the start of the time range
   */
  public LocalDateTime getStartDate() {
    return LocalDateTime.now().minus(amount, unit);
  }

  /**
   * Gets the end date for this time range, which is always the current time.
   *
   * @return LocalDateTime representing the current time
   */
  public LocalDateTime getEndDate() {
    return LocalDateTime.now();
  }

  /**
   * Creates a DateRange object for this time range.
   *
   * @return DateRange with start and end dates
   */
  public DateRange toDateRange() {
    return new DateRange(getStartDate(), getEndDate());
  }

  /** Record representing a date range with start and end dates. */
  public record DateRange(LocalDateTime start, LocalDateTime end) {}
}
