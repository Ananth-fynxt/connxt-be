package nexxus.session.service;

import nexxus.session.dto.FingerprintDto;
import nexxus.session.dto.SessionDto;

public interface WidgetSessionService {

  SessionDto.CreateResponse create(SessionDto.CreateRequest requestDto);

  SessionDto.RefreshResponse refresh(String currentToken, FingerprintDto fingerprint);

  SessionDto.ValidateResponse validate(String token, FingerprintDto fingerprint);

  void revoke(SessionDto.RevokeRequest requestDto);

  void revokeAll(String customerId, String brandId, String environmentId);
}
