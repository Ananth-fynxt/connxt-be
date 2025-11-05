package connxt.shared.filter.strategy;

import org.springframework.stereotype.Component;

import connxt.shared.config.RouteConfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SystemOnlyRouteStrategy implements RouteFilterStrategy {

  private final RouteConfig routeConfig;

  @Override
  public boolean canHandle(String requestUri) {
    return routeConfig.isSystemOnlyRoute(requestUri);
  }

  @Override
  public boolean process(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException {
    try {
      // These routes require SYSTEM scope authentication but no brand/environment context
      // They should already be authenticated by AccessTokenOncePerRequestFilter
      // Just proceed to the controller
      filterChain.doFilter(request, response);
      return false;
    } catch (Exception e) {
      throw new ServletException("Error processing system-only route", e);
    }
  }

  @Override
  public String getStrategyName() {
    return "SystemOnlyRouteStrategy";
  }
}
