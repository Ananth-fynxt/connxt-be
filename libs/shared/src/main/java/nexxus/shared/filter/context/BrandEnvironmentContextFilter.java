package nexxus.shared.filter.context;

import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import nexxus.shared.filter.strategy.RouteFilterStrategy;
import nexxus.shared.filter.strategy.RouteFilterStrategyFactory;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BrandEnvironmentContextFilter extends OncePerRequestFilter {

  private final RouteFilterStrategyFactory strategyFactory;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException {

    String requestUri = request.getRequestURI();

    try {
      RouteFilterStrategy strategy = strategyFactory.getStrategy(requestUri);

      if (strategy == null) {
        filterChain.doFilter(request, response);
        return;
      }

      strategy.process(request, response, filterChain);
    } catch (Exception e) {
      throw new ServletException("Error processing request", e);
    }
  }
}
