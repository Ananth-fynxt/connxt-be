package nexxus.riskrule.dto.validation;

import nexxus.riskrule.dto.RiskRuleDto;
import nexxus.shared.constants.RiskType;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCustomerCriteriaValidator
    implements ConstraintValidator<ValidCustomerCriteria, RiskRuleDto> {

  @Override
  public void initialize(ValidCustomerCriteria constraintAnnotation) {
    // No initialization needed
  }

  @Override
  public boolean isValid(RiskRuleDto dto, ConstraintValidatorContext context) {
    if (dto == null || dto.getType() == null) {
      return true; // Let other validators handle null checks
    }

    // If type is CUSTOMER, both criteriaType and criteriaValue must be provided
    if (RiskType.CUSTOMER.equals(dto.getType())) {
      boolean isValid =
          dto.getCriteriaType() != null
              && dto.getCriteriaValue() != null
              && !dto.getCriteriaValue().isEmpty();

      if (!isValid) {
        // Disable default constraint violation
        context.disableDefaultConstraintViolation();

        // Add custom constraint violations
        if (dto.getCriteriaType() == null) {
          context
              .buildConstraintViolationWithTemplate(
                  "criteriaType is required when type is CUSTOMER")
              .addPropertyNode("criteriaType")
              .addConstraintViolation();
        }

        if (dto.getCriteriaValue() == null || dto.getCriteriaValue().isEmpty()) {
          context
              .buildConstraintViolationWithTemplate(
                  "criteriaValue is required when type is CUSTOMER")
              .addPropertyNode("criteriaValue")
              .addConstraintViolation();
        }
      }

      return isValid;
    }

    // For DEFAULT type, criteria fields must NOT be provided
    if (RiskType.DEFAULT.equals(dto.getType())) {
      boolean hasCriteriaFields =
          dto.getCriteriaType() != null
              || (dto.getCriteriaValue() != null && !dto.getCriteriaValue().isEmpty());

      if (hasCriteriaFields) {
        // Disable default constraint violation
        context.disableDefaultConstraintViolation();

        // Add custom constraint violations
        context
            .buildConstraintViolationWithTemplate(
                "criteriaType and criteriaValue must not be provided when type is DEFAULT")
            .addPropertyNode("type")
            .addConstraintViolation();

        return false;
      }
    }

    return true;
  }
}
