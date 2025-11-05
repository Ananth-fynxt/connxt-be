package connxt.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EmailExecutionStatus {
  PENDING("PENDING"),
  SENDING("SENDING"),
  SENT("SENT"),
  DELIVERED("DELIVERED"),
  FAILED("FAILED"),
  RETRIED("RETRIED"),
  CANCELLED("CANCELLED"),
  REJECTED("REJECTED");

  private final String value;

  EmailExecutionStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
