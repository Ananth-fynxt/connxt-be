package nexxus.shared.validators;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.jamsesso.jsonlogic.JsonLogic;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public final class ConditionValidator {
  private final JsonLogic jsonLogic;
  private final ObjectMapper objectMapper;

  public ConditionValidator() {
    this.jsonLogic = new JsonLogic();
    this.objectMapper = new ObjectMapper();
  }

  public boolean matches(Object conditionJson, Map<String, Object> context) {
    try {
      if (conditionJson == null) {
        return true;
      }

      String conditionStr = convertToString(conditionJson);
      if (conditionStr == null
          || conditionStr.trim().isEmpty()
          || conditionStr.trim().equals("{}")) {
        return true;
      }

      Map<String, Object> safeContext = context != null ? context : Map.of();

      Object result = jsonLogic.apply(conditionStr, safeContext);

      return convertToBoolean(result);

    } catch (Exception ex) {
      log.error("Failed to evaluate condition: {}", conditionJson, ex);
      return false;
    }
  }

  public boolean matches(String conditionJson, Map<String, Object> transaction) {
    return matches((Object) conditionJson, transaction);
  }

  private String convertToString(Object conditionJson) {
    if (conditionJson instanceof String) {
      return (String) conditionJson;
    } else if (conditionJson instanceof JsonNode) {
      return conditionJson.toString();
    } else {
      try {
        return objectMapper.writeValueAsString(conditionJson);
      } catch (Exception e) {
        log.warn("Failed to convert condition to string: {}", conditionJson);
        return null;
      }
    }
  }

  private boolean convertToBoolean(Object result) {
    if (result instanceof Boolean) {
      return (Boolean) result;
    } else if (result instanceof Number) {
      return ((Number) result).doubleValue() != 0.0;
    } else if (result instanceof String) {
      return !((String) result).isEmpty() && !"false".equalsIgnoreCase((String) result);
    } else {
      return result != null;
    }
  }

  public boolean matches(JsonNode conditionJson, Map<String, Object> context) {
    return matches((Object) conditionJson, context);
  }
}
