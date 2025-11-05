package connxt.jwt.executor;

import connxt.jwt.dto.JwtTokenRequest;
import connxt.jwt.dto.JwtTokenResponse;
import connxt.jwt.dto.JwtValidationRequest;
import connxt.jwt.dto.JwtValidationResponse;

public interface JwtExecutor {

  JwtTokenResponse generateToken(JwtTokenRequest request);

  JwtValidationResponse validateToken(JwtValidationRequest request);
}
