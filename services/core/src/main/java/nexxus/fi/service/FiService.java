package nexxus.fi.service;

import nexxus.fi.dto.FiDto;

public interface FiService {

  FiDto create(FiDto fiDto);

  FiDto findByUserId(String userId);
}
