package nexxus.shared.util;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpStatus;

import nexxus.shared.constants.ErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Utility class for writing consistent error responses across filters and interceptors Eliminates
 * code duplication and ensures consistent error response format
 */
public final class ErrorResponseUtil {

  private ErrorResponseUtil() {
    // Utility class - prevent instantiation
  }

  /**
   * Writes a standardized error response with timestamp
   *
   * @param response the HTTP response object
   * @param errorCode the error code from ErrorCode enum
   * @param status the HTTP status code
   * @throws IOException if writing to response fails
   */
  public static void writeErrorResponse(
      HttpServletResponse response, ErrorCode errorCode, HttpStatus status) throws IOException {
    writeErrorResponse(null, response, errorCode, status);
  }

  /**
   * Writes a standardized error response with timestamp and CORS headers
   *
   * @param request the HTTP request object (can be null)
   * @param response the HTTP response object
   * @param errorCode the error code from ErrorCode enum
   * @param status the HTTP status code
   * @throws IOException if writing to response fails
   */
  public static void writeErrorResponse(
      HttpServletRequest request,
      HttpServletResponse response,
      ErrorCode errorCode,
      HttpStatus status)
      throws IOException {

    response.setStatus(status.value());
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    String timestamp =
        OffsetDateTime.now(ZoneOffset.UTC)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

    String jsonResponse =
        String.format(
            "{\"timestamp\":\"%s\",\"code\":\"%s\",\"message\":\"%s\"}",
            timestamp, errorCode.getCode(), errorCode.getMessage());

    response.getWriter().write(jsonResponse);
  }

  /**
   * Writes a standardized error response with timestamp using generic message
   *
   * @param response the HTTP response object
   * @param message the error message
   * @param status the HTTP status code
   * @throws IOException if writing to response fails
   */
  public static void writeErrorResponse(
      HttpServletResponse response, String message, HttpStatus status) throws IOException {
    writeErrorResponse(null, response, message, status);
  }

  /**
   * Writes a standardized error response with timestamp using generic message and CORS headers
   *
   * @param request the HTTP request object (can be null)
   * @param response the HTTP response object
   * @param message the error message
   * @param status the HTTP status code
   * @throws IOException if writing to response fails
   */
  public static void writeErrorResponse(
      HttpServletRequest request, HttpServletResponse response, String message, HttpStatus status)
      throws IOException {

    response.setStatus(status.value());
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    String timestamp =
        OffsetDateTime.now(ZoneOffset.UTC)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

    String jsonResponse =
        String.format("{\"timestamp\":\"%s\",\"message\":\"%s\"}", timestamp, message);

    response.getWriter().write(jsonResponse);
  }
}
