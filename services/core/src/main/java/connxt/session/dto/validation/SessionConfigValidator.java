package connxt.session.dto.validation;

import connxt.session.dto.SessionDto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SessionConfigValidator
    implements ConstraintValidator<ValidSessionConfig, SessionDto.Config> {

  @Override
  public boolean isValid(SessionDto.Config config, ConstraintValidatorContext context) {
    if (config == null) {
      return true; // Optional field
    }

    // Validate timeout minutes (1-1440 minutes = 1 minute to 24 hours)
    if (config.getTimeoutMinutes() == null
        || config.getTimeoutMinutes() < 1
        || config.getTimeoutMinutes() > 1440) {
      return false;
    }

    // Validate max extensions (0-10)
    if (config.getMaxExtensions() == null
        || config.getMaxExtensions() < 0
        || config.getMaxExtensions() > 10) {
      return false;
    }

    return true;
  }
}
