package nexxus.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RoutingDuration {
  HOUR("HOUR"),
  DAY("DAY"),
  WEEK("WEEK"),
  MONTH("MONTH");

  private final String value;

  RoutingDuration(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
