package nexxus.shared.validators;

import java.util.List;

import lombok.Getter;

@Getter
public class SchemaValidationException extends RuntimeException {

  private final List<String> validationErrors;

  public SchemaValidationException(String message, List<String> validationErrors) {
    super(message + ": " + String.join("; ", validationErrors));
    this.validationErrors = validationErrors;
  }

  public SchemaValidationException(String message, String validationError) {
    this(message, List.of(validationError));
  }

  public SchemaValidationException(String message) {
    this(message, List.of());
  }
}
