package nexxus.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AuthType {
  APPLICATION_USER("APPLICATION_USER"),
  EXTERNAL_API("EXTERNAL_API");

  private final String value;

  AuthType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
