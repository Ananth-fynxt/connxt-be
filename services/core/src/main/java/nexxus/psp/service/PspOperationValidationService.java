package nexxus.psp.service;

import java.util.List;

import nexxus.psp.entity.Psp;
import nexxus.request.dto.RequestInputDto;

public interface PspOperationValidationService {
  boolean isPspOperationValid(Psp psp, RequestInputDto request);

  List<Psp> filterValidPspOperations(List<Psp> psps, RequestInputDto request);
}
