package nexxus.psp.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import nexxus.psp.entity.MaintenanceWindow;
import nexxus.psp.entity.Psp;
import nexxus.psp.repository.MaintenanceWindowRepository;
import nexxus.psp.service.MaintenanceWindowService;
import nexxus.shared.constants.Status;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaintenanceWindowServiceImpl implements MaintenanceWindowService {

  private final MaintenanceWindowRepository maintenanceWindowRepository;

  @Override
  public boolean isPspInMaintenance(Psp psp, String flowActionId) {
    List<MaintenanceWindow> maintenanceWindows =
        maintenanceWindowRepository.findByPspIdAndFlowActionId(psp.getId(), flowActionId);

    if (CollectionUtils.isEmpty(maintenanceWindows)) {
      log.debug(
          "No maintenance windows found for PSP: {} and flow action: {}",
          psp.getId(),
          flowActionId);
      return false;
    }

    LocalDateTime now = LocalDateTime.now();
    boolean isInMaintenance =
        maintenanceWindows.stream()
            .anyMatch(
                window ->
                    Status.ENABLED.equals(window.getStatus())
                        && window.getStartAt().isBefore(now)
                        && window.getEndAt().isAfter(now));

    if (isInMaintenance) {
      log.debug("PSP: {} is in maintenance for flow action: {}", psp.getId(), flowActionId);
    }

    return isInMaintenance;
  }

  @Override
  public List<Psp> filterPspsNotInMaintenance(List<Psp> psps, String flowActionId) {
    return psps.stream()
        .filter(psp -> !isPspInMaintenance(psp, flowActionId))
        .collect(Collectors.toList());
  }
}
