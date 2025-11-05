package nexxus.shared.validators;

import java.util.Collections;
import java.util.List;

public record ValidationResult(boolean valid, List<String> errors) {

  public static ValidationResult success() {
    return new ValidationResult(true, Collections.emptyList());
  }

  public static ValidationResult failure(List<String> errors) {
    return new ValidationResult(false, errors);
  }

  public static ValidationResult failure(String error) {
    return new ValidationResult(false, List.of(error));
  }

  @Override
  public boolean valid() {
    return valid;
  }

  public String getFirstError() {
    return errors.isEmpty() ? null : errors.get(0);
  }

  public String getErrorsAsString() {
    return String.join("; ", errors);
  }
}
