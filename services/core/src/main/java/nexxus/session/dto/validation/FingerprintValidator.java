package nexxus.session.dto.validation;

import nexxus.session.dto.FingerprintDto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FingerprintValidator implements ConstraintValidator<ValidFingerprint, FingerprintDto> {

  @Override
  public boolean isValid(FingerprintDto fingerprint, ConstraintValidatorContext context) {
    if (fingerprint == null) {
      return false;
    }

    // Validate required fields
    if (fingerprint.getUserAgent() == null || fingerprint.getUserAgent().trim().isEmpty()) {
      return false;
    }

    if (fingerprint.getDeviceId() == null || fingerprint.getDeviceId().trim().isEmpty()) {
      return false;
    }

    if (fingerprint.getPlatform() == null || fingerprint.getPlatform().trim().isEmpty()) {
      return false;
    }

    if (fingerprint.getLanguage() == null || fingerprint.getLanguage().trim().isEmpty()) {
      return false;
    }

    if (fingerprint.getTimezone() == null || fingerprint.getTimezone().trim().isEmpty()) {
      return false;
    }

    // Validate hardware concurrency (must be positive)
    if (fingerprint.getHardwareConcurrency() != null && fingerprint.getHardwareConcurrency() <= 0) {
      return false;
    }

    // Validate device memory (must be positive)
    if (fingerprint.getDeviceMemory() != null && fingerprint.getDeviceMemory() <= 0) {
      return false;
    }

    return true;
  }
}
