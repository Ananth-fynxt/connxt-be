package nexxus.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Scope {
  SYSTEM("SYSTEM"),
  FI("FI"),
  BRAND("BRAND"),
  EXTERNAL("EXTERNAL");

  private final String value;

  Scope(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
