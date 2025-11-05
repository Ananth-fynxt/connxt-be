package connxt.fi.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import connxt.fi.dto.FiDto;
import connxt.fi.entity.Fi;
import connxt.fi.repository.FiRepository;
import connxt.fi.service.FiService;
import connxt.fi.service.mappers.FiMapper;
import connxt.shared.constants.ErrorCode;
import connxt.shared.service.NameUniquenessService;
import connxt.user.dto.UserRequest;
import connxt.user.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FiServiceImpl implements FiService {

  private final FiRepository fiRepository;
  private final FiMapper fiMapper;
  private final UserService userService;
  private final NameUniquenessService nameUniquenessService;

  @Override
  @Transactional
  public FiDto create(FiDto dto) {
    nameUniquenessService.validateForCreate(
        name -> fiRepository.existsByName(name), "FI", dto.getName());
    verifyFiEmailExists(dto.getEmail());

    UserRequest createUserRequest = UserRequest.builder().email(dto.getEmail()).build();

    UserRequest createdUser = userService.createUser(createUserRequest);

    Fi fi = fiMapper.toFi(dto);
    fi.setUserId(createdUser.getId());

    return fiMapper.toFiDto(fiRepository.save(fi));
  }

  private void verifyFiEmailExists(String email) {
    if (fiRepository.existsByEmail(email)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, ErrorCode.FI_EMAIL_ALREADY_EXISTS.getCode());
    }
  }

  @Override
  public FiDto findByUserId(String userId) {
    Fi fi =
        fiRepository
            .findByUserId(userId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, ErrorCode.FI_NOT_FOUND.getCode()));
    return fiMapper.toFiDto(fi);
  }
}
