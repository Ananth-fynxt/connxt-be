package nexxus.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TokenType {
  INVITATION("INVITATION"),
  RESET_PASSWORD("RESET_PASSWORD"),
  ACCESS("ACCESS"),
  REFRESH("REFRESH");

  private final String value;

  TokenType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
