package nexxus.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RiskType {
  DEFAULT("DEFAULT"),
  CUSTOMER("CUSTOMER");

  private final String value;

  RiskType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
