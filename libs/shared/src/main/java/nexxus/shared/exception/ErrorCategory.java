package nexxus.shared.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCategory {
  NOT_FOUND(HttpStatus.NOT_FOUND),
  INTERNAL(HttpStatus.INTERNAL_SERVER_ERROR),
  DUPLICATE(HttpStatus.CONFLICT);

  private final HttpStatus http;

  ErrorCategory(HttpStatus http) {
    this.http = http;
  }

  public HttpStatus http() {
    return http;
  }
}
