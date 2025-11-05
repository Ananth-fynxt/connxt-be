package connxt.request.service;

import connxt.request.dto.RequestInputDto;
import connxt.request.dto.RequestOutputDto;

public interface RequestService {

  RequestOutputDto fetchPsp(RequestInputDto requestInputDto);
}
