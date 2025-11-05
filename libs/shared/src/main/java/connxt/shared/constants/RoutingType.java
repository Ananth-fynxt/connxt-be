package connxt.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RoutingType {
  AMOUNT("AMOUNT"),
  PERCENTAGE("PERCENTAGE"),
  COUNT("COUNT");

  private final String value;

  RoutingType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
