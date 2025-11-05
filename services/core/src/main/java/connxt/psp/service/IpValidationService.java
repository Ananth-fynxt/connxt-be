package connxt.psp.service;

import java.util.List;

import connxt.psp.entity.Psp;
import connxt.request.dto.RequestInputDto;

public interface IpValidationService {
  boolean isIpValid(Psp psp, RequestInputDto request);

  List<Psp> filterValidIps(List<Psp> psps, RequestInputDto request);
}
