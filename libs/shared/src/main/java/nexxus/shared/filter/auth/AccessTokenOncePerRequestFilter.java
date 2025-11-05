package nexxus.shared.filter.auth;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import nexxus.shared.config.RouteConfig;
import nexxus.shared.constants.ErrorCode;
import nexxus.shared.util.ErrorResponseUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccessTokenOncePerRequestFilter extends OncePerRequestFilter {

  private final RouteConfig routeConfig;
  private final List<AuthenticationStrategy> authenticationStrategies;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    if (routeConfig.isPublic(request.getRequestURI())) {
      filterChain.doFilter(request, response);
      return;
    }

    AuthenticationStrategy strategy = findAuthenticationStrategy(request);
    if (strategy == null) {
      ErrorResponseUtil.writeErrorResponse(
          request, response, ErrorCode.AUTHENTICATION_REQUIRED, HttpStatus.UNAUTHORIZED);
      return;
    }

    if (!strategy.validate(request, response)) {
      return;
    }

    filterChain.doFilter(request, response);
  }

  private AuthenticationStrategy findAuthenticationStrategy(HttpServletRequest request) {
    return authenticationStrategies.stream()
        .filter(strategy -> strategy.canHandle(request))
        .findFirst()
        .orElse(null);
  }
}
