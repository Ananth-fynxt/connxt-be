package nexxus.psp.service;

import java.util.List;

import nexxus.psp.entity.Psp;
import nexxus.request.dto.RequestInputDto;

public interface IpValidationService {
  boolean isIpValid(Psp psp, RequestInputDto request);

  List<Psp> filterValidIps(List<Psp> psps, RequestInputDto request);
}
