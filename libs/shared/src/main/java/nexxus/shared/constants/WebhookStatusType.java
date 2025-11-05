package nexxus.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WebhookStatusType {
  SUCCESS("SUCCESS"),
  FAILURE("FAILURE"),
  NOTIFICATION("NOTIFICATION");

  private final String value;

  WebhookStatusType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
