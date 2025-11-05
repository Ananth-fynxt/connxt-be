package connxt.session.dto.validation;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = SessionConfigValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSessionConfig {
  String message() default "Invalid session configuration";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
