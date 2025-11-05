package nexxus.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ChargeFeeType {
  INCLUSIVE("INCLUSIVE"),
  EXCLUSIVE("EXCLUSIVE");

  private final String value;

  ChargeFeeType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
