package connxt.routingrule.dto.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = PspSelectionModeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PspSelectionModeValidation {

  String message() default "Invalid PSP selection mode configuration";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
