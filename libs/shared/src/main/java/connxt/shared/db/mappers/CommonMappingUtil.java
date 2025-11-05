package connxt.shared.db.mappers;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommonMappingUtil {

  private final ObjectMapper objectMapper;

  public JsonNode stringToJsonNode(String json) {
    if (json == null) return null;
    try {
      return objectMapper.readTree(json);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Invalid JSON: " + json, e);
    }
  }

  public String jsonNodeToString(JsonNode jsonNode) {
    return jsonNode != null ? jsonNode.toString() : null;
  }

  public String[] map(List<String> list) {
    return list == null ? null : list.toArray(new String[0]);
  }

  public List<String> map(String[] array) {
    return array == null ? null : Arrays.asList(array);
  }

  public Float integerToFloat(Integer value) {
    return value != null ? value.floatValue() : 0.0f;
  }

  public Integer floatToInteger(Float value) {
    return value != null ? Math.round(value) : null;
  }

  public JsonNode map(Object conditionJson) {
    if (conditionJson == null) {
      return null;
    }

    try {
      if (conditionJson instanceof JsonNode) {
        return (JsonNode) conditionJson;
      } else if (conditionJson instanceof String) {
        return objectMapper.readTree((String) conditionJson);
      } else {
        return objectMapper.valueToTree(conditionJson);
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to convert Object to JsonNode", e);
    }
  }
}
