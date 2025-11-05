package nexxus.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionStatus {
  INITIATED("INITIATED"),
  EVALUATED("EVALUATED"),
  PSP_OFFERED("PSP_OFFERED"),
  PSP_SELECTED("PSP_SELECTED"),
  PROCESSING("PROCESSING"),
  COMPLETED("COMPLETED"),
  FAILED("FAILED"),
  CANCELLED("CANCELLED"),
  ABANDONED("ABANDONED");

  private final String value;

  TransactionStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }
}
