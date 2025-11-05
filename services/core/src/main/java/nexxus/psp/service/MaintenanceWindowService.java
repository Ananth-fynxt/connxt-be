package nexxus.psp.service;

import java.util.List;

import nexxus.psp.entity.Psp;

public interface MaintenanceWindowService {
  boolean isPspInMaintenance(Psp psp, String flowActionId);

  List<Psp> filterPspsNotInMaintenance(List<Psp> psps, String flowActionId);
}
