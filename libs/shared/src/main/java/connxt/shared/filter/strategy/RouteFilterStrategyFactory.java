package connxt.shared.filter.strategy;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RouteFilterStrategyFactory {

  private final List<RouteFilterStrategy> strategies;

  public RouteFilterStrategy getStrategy(String requestUri) {
    for (RouteFilterStrategy strategy : strategies) {
      if (strategy.canHandle(requestUri)) {
        return strategy;
      }
    }

    return null;
  }
}
