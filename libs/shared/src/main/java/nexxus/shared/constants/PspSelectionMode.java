package nexxus.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PspSelectionMode {
  PRIORITY("PRIORITY"),
  WEIGHTAGE("WEIGHTAGE");

  private final String value;

  PspSelectionMode(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
