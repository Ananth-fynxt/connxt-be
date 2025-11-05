package nexxus.shared.validators;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = JsonValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidJson {
  String message() default "Invalid JSON format";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
