package connxt.psp.service;

import java.util.List;

import connxt.psp.entity.Psp;
import connxt.request.dto.RequestInputDto;

public interface PspOperationValidationService {
  boolean isPspOperationValid(Psp psp, RequestInputDto request);

  List<Psp> filterValidPspOperations(List<Psp> psps, RequestInputDto request);
}
