package connxt.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FeeComponentType {
  FIXED("FIXED"),
  PERCENTAGE("PERCENTAGE");

  private final String value;

  FeeComponentType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
