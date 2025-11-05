package nexxus.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TokenStatus {
  ACTIVE("ACTIVE"),
  REVOKED("REVOKED"),
  EXPIRED("EXPIRED");

  private final String value;

  TokenStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
