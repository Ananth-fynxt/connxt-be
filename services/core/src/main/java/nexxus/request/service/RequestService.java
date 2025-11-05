package nexxus.request.service;

import nexxus.request.dto.RequestInputDto;
import nexxus.request.dto.RequestOutputDto;

public interface RequestService {

  RequestOutputDto fetchPsp(RequestInputDto requestInputDto);
}
