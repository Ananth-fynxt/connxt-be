package nexxus.riskrule.dto.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCustomerCriteriaValidator.class)
public @interface ValidCustomerCriteria {
  String message() default "Invalid criteria configuration based on risk rule type";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
