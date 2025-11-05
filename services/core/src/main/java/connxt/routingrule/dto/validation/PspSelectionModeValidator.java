package connxt.routingrule.dto.validation;

import connxt.shared.constants.PspSelectionMode;
import connxt.shared.constants.RoutingDuration;
import connxt.shared.constants.RoutingType;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PspSelectionModeValidator
    implements ConstraintValidator<PspSelectionModeValidation, Object> {

  @Override
  public void initialize(PspSelectionModeValidation constraintAnnotation) {
    // No initialization needed
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    if (value == null) {
      return true; // Let @NotNull handle null values
    }

    try {
      // Use reflection to get the fields
      PspSelectionMode pspSelectionMode =
          getFieldValue(value, "pspSelectionMode", PspSelectionMode.class);
      RoutingType routingType = getFieldValue(value, "routingType", RoutingType.class);
      RoutingDuration duration = getFieldValue(value, "duration", RoutingDuration.class);

      if (pspSelectionMode == null) {
        return true; // Let @NotNull handle null PSP selection mode
      }

      // PRIORITY mode: routingType and duration should be NULL
      if (pspSelectionMode == PspSelectionMode.PRIORITY) {
        if (routingType != null || duration != null) {
          context.disableDefaultConstraintViolation();
          context
              .buildConstraintViolationWithTemplate(
                  "For PRIORITY mode, routingType and duration must be null")
              .addConstraintViolation();
          return false;
        }
      }

      // WEIGHTAGE mode: routingType and duration should have values
      if (pspSelectionMode == PspSelectionMode.WEIGHTAGE) {
        if (routingType == null || duration == null) {
          context.disableDefaultConstraintViolation();
          context
              .buildConstraintViolationWithTemplate(
                  "For WEIGHTAGE mode, routingType and duration are required")
              .addConstraintViolation();
          return false;
        }
      }

      return true;
    } catch (Exception e) {
      // If reflection fails, let other validations handle it
      return true;
    }
  }

  @SuppressWarnings("unchecked")
  private <T> T getFieldValue(Object obj, String fieldName, Class<T> fieldType) {
    try {
      var field = obj.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      Object value = field.get(obj);
      return (T) value;
    } catch (Exception e) {
      return null;
    }
  }
}
