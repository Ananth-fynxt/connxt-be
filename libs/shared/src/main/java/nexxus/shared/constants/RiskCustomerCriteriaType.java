package nexxus.shared.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RiskCustomerCriteriaType {
  TAG("TAG"),
  ACCOUNT_TYPE("ACCOUNT_TYPE");

  private final String value;

  RiskCustomerCriteriaType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
