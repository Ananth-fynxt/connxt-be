package nexxus.fee.dto.validation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import nexxus.fee.dto.FeeComponentDto;
import nexxus.shared.constants.FeeComponentType;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FeeComponentsValidator
    implements ConstraintValidator<ValidFeeComponents, List<FeeComponentDto>> {

  @Override
  public boolean isValid(List<FeeComponentDto> components, ConstraintValidatorContext context) {
    if (components == null || components.isEmpty()) {
      return false;
    }

    Set<FeeComponentType> allowedTypes =
        Set.of(FeeComponentType.FIXED, FeeComponentType.PERCENTAGE);

    for (FeeComponentDto component : components) {
      if (component.getType() == null || !allowedTypes.contains(component.getType())) {
        return false;
      }
      if (component.getAmount() == null || component.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
        return false;
      }
      if (component.getMinValue() != null && component.getMaxValue() != null) {
        if (component.getMinValue().compareTo(component.getMaxValue()) > 0) {
          return false;
        }
      }
    }

    // Check for duplicate component types
    long uniqueComponentCount =
        components.stream().map(FeeComponentDto::getType).distinct().count();

    return uniqueComponentCount == components.size();
  }
}
