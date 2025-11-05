package nexxus.jwt.executor;

import nexxus.jwt.dto.JwtTokenRequest;
import nexxus.jwt.dto.JwtTokenResponse;
import nexxus.jwt.dto.JwtValidationRequest;
import nexxus.jwt.dto.JwtValidationResponse;

public interface JwtExecutor {

  JwtTokenResponse generateToken(JwtTokenRequest request);

  JwtValidationResponse validateToken(JwtValidationRequest request);
}
