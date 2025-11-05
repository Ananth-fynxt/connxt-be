package connxt.shared.db;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<String> {

  @Override
  @NonNull
  public Optional<String> getCurrentAuditor() {
    return Optional.ofNullable(
        SecurityContextHolder.getContext().getAuthentication() != null
            ? SecurityContextHolder.getContext().getAuthentication().getName()
            : "system");
  }
}
