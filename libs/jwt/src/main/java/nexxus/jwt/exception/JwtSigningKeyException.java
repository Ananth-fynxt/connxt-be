package nexxus.jwt.exception;

public class JwtSigningKeyException extends RuntimeException {

  public JwtSigningKeyException(String message) {
    super(message);
  }

  public JwtSigningKeyException(String message, Throwable cause) {
    super(message, cause);
  }
}
