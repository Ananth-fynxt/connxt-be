package connxt.psp.service;

import java.util.List;

import connxt.psp.entity.Psp;
import connxt.request.dto.RequestInputDto;

public interface FailureRateValidationService {
  boolean isFailureRateValid(Psp psp, RequestInputDto request);

  List<Psp> filterValidFailureRates(List<Psp> psps, RequestInputDto request);
}
