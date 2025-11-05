package nexxus.psp.service.filter;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PspFilterPipeline {

  private final List<PspFilterStrategy> filterStrategies;

  public PspFilterContext applyFilters(PspFilterContext context) {
    log.info("Starting PSP filtering pipeline with {} PSPs", context.getFilteredPsps().size());

    List<PspFilterStrategy> sortedStrategies =
        filterStrategies.stream()
            .filter(strategy -> strategy.shouldApply(context))
            .sorted(Comparator.comparing(PspFilterStrategy::getPriority))
            .toList();

    log.debug("Applying {} filter strategies in order", sortedStrategies.size());

    PspFilterContext currentContext = context;

    for (PspFilterStrategy strategy : sortedStrategies) {
      int beforeCount = currentContext.getFilteredPsps().size();
      log.debug(
          "Applying filter: {} (priority: {})", strategy.getStrategyName(), strategy.getPriority());

      currentContext = strategy.apply(currentContext);

      int afterCount = currentContext.getFilteredPsps().size();
      int filteredOut = beforeCount - afterCount;

      log.info(
          "Filter '{}' completed: {} PSPs remaining ({} filtered out)",
          strategy.getStrategyName(),
          afterCount,
          filteredOut);

      currentContext.addFilterMetadata(strategy.getStrategyName() + "_filtered_count", filteredOut);

      if (currentContext.getFilteredPsps().isEmpty()) {
        log.warn("No PSPs remaining after applying filter: {}", strategy.getStrategyName());
        break;
      }
    }

    log.info(
        "PSP filtering pipeline completed: {} PSPs remaining from {} original",
        currentContext.getFilteredPsps().size(),
        context.getOriginalPsps().size());

    return currentContext;
  }
}
