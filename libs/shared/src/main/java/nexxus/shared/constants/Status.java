package nexxus.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
  ENABLED("ENABLED"),
  DISABLED("DISABLED");

  private final String value;

  Status(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
