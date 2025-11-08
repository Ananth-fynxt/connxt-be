package connxt.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RoleType {
  ADMIN("ADMIN"),
  MANAGER("MANAGER"),
  VIEWER("VIEWER");

  private final String value;

  RoleType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
