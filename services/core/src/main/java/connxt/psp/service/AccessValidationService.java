package connxt.psp.service;

import java.util.List;

import connxt.psp.entity.Psp;
import connxt.request.dto.RequestInputDto;

public interface AccessValidationService {
  boolean isAccessValid(Psp psp, RequestInputDto request);

  List<Psp> filterValidAccess(List<Psp> psps, RequestInputDto request);
}
