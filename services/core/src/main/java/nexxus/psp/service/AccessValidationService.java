package nexxus.psp.service;

import java.util.List;

import nexxus.psp.entity.Psp;
import nexxus.request.dto.RequestInputDto;

public interface AccessValidationService {
  boolean isAccessValid(Psp psp, RequestInputDto request);

  List<Psp> filterValidAccess(List<Psp> psps, RequestInputDto request);
}
