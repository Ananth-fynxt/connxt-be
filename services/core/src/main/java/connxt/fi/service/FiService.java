package connxt.fi.service;

import connxt.fi.dto.FiDto;

public interface FiService {

  FiDto create(FiDto fiDto);

  FiDto findByUserId(String userId);
}
