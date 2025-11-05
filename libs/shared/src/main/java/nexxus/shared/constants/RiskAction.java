package nexxus.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RiskAction {
  BLOCK("BLOCK"),
  ALERT("ALERT");

  private final String value;

  RiskAction(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
