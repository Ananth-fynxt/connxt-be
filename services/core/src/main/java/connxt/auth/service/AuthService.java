package connxt.auth.service;

import connxt.auth.dto.AuthResponse;
import connxt.auth.dto.LoginRequest;

public interface AuthService {

  AuthResponse login(LoginRequest request);

  AuthResponse refreshToken(String refreshToken);

  String logout(String authorization);
}
