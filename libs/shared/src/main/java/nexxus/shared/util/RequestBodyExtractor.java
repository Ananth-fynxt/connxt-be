package nexxus.shared.util;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for extracting request body from HTTP requests Supports both JSON and form-encoded
 * content types
 */
@Slf4j
@Component
public class RequestBodyExtractor {

  private final ObjectMapper objectMapper;

  public RequestBodyExtractor() {
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Extracts request body from HttpServletRequest Handles both application/json and
   * application/x-www-form-urlencoded content types
   *
   * @param request the HttpServletRequest
   * @return Map containing the extracted data, empty map if extraction fails
   */
  public Map<String, Object> extractRequestBody(HttpServletRequest request) {
    try {
      String contentType = request.getContentType();
      if (contentType == null || contentType.isEmpty()) {
        return new HashMap<>();
      }

      // Handle form-encoded data
      if (contentType.contains("application/x-www-form-urlencoded")) {
        return extractFormData(request);
      }

      // Handle JSON data
      if (contentType.contains("application/json")) {
        return extractJsonData(request);
      }

      return new HashMap<>();
    } catch (Exception e) {
      log.warn("Failed to extract request body: {}", e.getMessage());
      return new HashMap<>();
    }
  }

  /** Extracts form-encoded data from request parameters */
  private Map<String, Object> extractFormData(HttpServletRequest request) {
    Map<String, Object> formData = new HashMap<>();
    request
        .getParameterMap()
        .forEach(
            (key, values) -> {
              if (values.length == 1) {
                formData.put(key, values[0]);
              } else {
                formData.put(key, values);
              }
            });
    return formData;
  }

  /** Extracts JSON data from request body */
  @SuppressWarnings("unchecked")
  private Map<String, Object> extractJsonData(HttpServletRequest request) {
    try (BufferedReader reader = request.getReader()) {
      StringBuilder jsonBuilder = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        jsonBuilder.append(line);
      }
      String jsonString = jsonBuilder.toString();
      if (jsonString.isEmpty()) {
        return new HashMap<>();
      }

      return objectMapper.readValue(jsonString, Map.class);
    } catch (Exception e) {
      log.warn("Failed to parse JSON request body: {}", e.getMessage());
      return new HashMap<>();
    }
  }
}
