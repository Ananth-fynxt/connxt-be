package connxt.session.dto.validation;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = FingerprintValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFingerprint {
  String message() default "Invalid fingerprint data";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
