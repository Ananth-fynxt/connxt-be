package nexxus.psp.service;

import java.util.List;

import nexxus.psp.entity.Psp;
import nexxus.request.dto.RequestInputDto;

public interface FailureRateValidationService {
  boolean isFailureRateValid(Psp psp, RequestInputDto request);

  List<Psp> filterValidFailureRates(List<Psp> psps, RequestInputDto request);
}
