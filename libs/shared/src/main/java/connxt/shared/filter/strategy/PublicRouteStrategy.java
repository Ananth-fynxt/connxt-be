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
public class PublicRouteStrategy implements RouteFilterStrategy {

  private final RouteConfig routeConfig;

  @Override
  public boolean canHandle(String requestUri) {
    return routeConfig.isPublic(requestUri);
  }

  @Override
  public boolean process(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException {
    try {
      // Public routes bypass all security checks and proceed directly to the controller
      filterChain.doFilter(request, response);
      return false;
    } catch (Exception e) {
      throw new ServletException("Error processing public route", e);
    }
  }

  @Override
  public String getStrategyName() {
    return "PublicRouteStrategy";
  }
}
