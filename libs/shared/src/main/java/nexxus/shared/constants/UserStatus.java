package nexxus.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatus {
  INVITED("INVITED"),
  ACTIVE("ACTIVE"),
  INACTIVE("INACTIVE");

  private final String value;

  UserStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
