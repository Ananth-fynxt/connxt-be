package nexxus.auth.service;

import nexxus.auth.dto.AuthResponse;
import nexxus.auth.dto.ExternalLoginRequest;
import nexxus.auth.dto.LoginRequest;

public interface AuthService {

  AuthResponse login(LoginRequest request);

  AuthResponse externalLogin(ExternalLoginRequest request);

  AuthResponse refreshToken(String refreshToken);

  String logout(String authorization);
}
