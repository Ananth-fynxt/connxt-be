package connxt.shared.validators;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class JsonValidator implements ConstraintValidator<ValidJson, String> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    try {
      objectMapper.readTree(value);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
