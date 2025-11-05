package nexxus.psp.service.filter.strategy;

import java.util.List;

import org.springframework.stereotype.Component;

import nexxus.psp.entity.Psp;
import nexxus.psp.service.AccessValidationService;
import nexxus.psp.service.FailureRateValidationService;
import nexxus.psp.service.IpValidationService;
import nexxus.psp.service.MaintenanceWindowService;
import nexxus.psp.service.PspOperationValidationService;
import nexxus.psp.service.filter.PspFilterContext;
import nexxus.psp.service.filter.PspFilterStrategy;
import nexxus.request.dto.RequestInputDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PspConfigurationFilterStrategy implements PspFilterStrategy {

  private final MaintenanceWindowService maintenanceWindowService;
  private final PspOperationValidationService pspOperationValidationService;
  private final IpValidationService ipValidationService;
  private final AccessValidationService accessValidationService;
  private final FailureRateValidationService failureRateValidationService;

  @Override
  public PspFilterContext apply(PspFilterContext context) {
    List<Psp> currentPsps = context.getFilteredPsps();
    log.debug("Applying PSP configuration filter to {} PSPs", currentPsps.size());

    List<Psp> filteredPsps = applyFilters(currentPsps, context.getRequest());

    log.debug("After PSP configuration filtering: {} PSPs remaining", filteredPsps.size());

    context.updateFilteredPsps(filteredPsps);
    context.addFilterMetadata(
        "psp_configuration_filtered_count", currentPsps.size() - filteredPsps.size());

    return context;
  }

  private List<Psp> applyFilters(List<Psp> psps, RequestInputDto request) {
    List<Psp> filteredPsps = psps;

    filteredPsps =
        maintenanceWindowService.filterPspsNotInMaintenance(filteredPsps, request.getActionId());
    log.debug("After maintenance window filter: {} PSPs remaining", filteredPsps.size());
    if (filteredPsps.isEmpty()) return filteredPsps;

    filteredPsps = pspOperationValidationService.filterValidPspOperations(filteredPsps, request);
    log.debug("After PSP operations filter: {} PSPs remaining", filteredPsps.size());
    if (filteredPsps.isEmpty()) return filteredPsps;

    filteredPsps = ipValidationService.filterValidIps(filteredPsps, request);
    log.debug("After IP validation filter: {} PSPs remaining", filteredPsps.size());
    if (filteredPsps.isEmpty()) return filteredPsps;

    filteredPsps = accessValidationService.filterValidAccess(filteredPsps, request);
    log.debug("After access validation filter: {} PSPs remaining", filteredPsps.size());
    if (filteredPsps.isEmpty()) return filteredPsps;

    filteredPsps = failureRateValidationService.filterValidFailureRates(filteredPsps, request);
    log.debug("After failure rate filter: {} PSPs remaining", filteredPsps.size());

    return filteredPsps;
  }

  @Override
  public int getPriority() {
    return 3; // Third priority - apply after risk rules and transaction limits
  }

  @Override
  public String getStrategyName() {
    return "PspConfigurationFilter";
  }

  @Override
  public boolean shouldApply(PspFilterContext context) {
    return true;
  }
}
