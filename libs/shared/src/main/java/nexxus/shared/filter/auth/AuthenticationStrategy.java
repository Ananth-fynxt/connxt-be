package nexxus.shared.filter.auth;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationStrategy {

  boolean validate(HttpServletRequest request, HttpServletResponse response) throws IOException;

  boolean canHandle(HttpServletRequest request);
}
