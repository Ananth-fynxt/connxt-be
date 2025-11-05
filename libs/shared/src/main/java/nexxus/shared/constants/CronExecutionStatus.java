package nexxus.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CronExecutionStatus {
  PENDING("PENDING"),
  IN_PROGRESS("IN_PROGRESS"),
  SUCCESS("SUCCESS"),
  FAILED("FAILED"),
  RETRIED("RETRIED"),
  CANCELLED("CANCELLED");

  private final String value;

  CronExecutionStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
