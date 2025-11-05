package nexxus.shared.filter.strategy;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface RouteFilterStrategy {

  boolean canHandle(String requestUri);

  boolean process(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws Exception;

  String getStrategyName();
}
