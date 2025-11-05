package nexxus.denovm.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DenoExecutorMode {
  SINGLE("SINGLE"),
  WORKER("WORKER");

  private final String value;

  DenoExecutorMode(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
