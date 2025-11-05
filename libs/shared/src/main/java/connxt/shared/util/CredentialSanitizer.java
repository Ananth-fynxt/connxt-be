package connxt.shared.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CredentialSanitizer {

  private final ObjectMapper objectMapper;

  public CredentialSanitizer() {
    this.objectMapper = new ObjectMapper();
  }

  public Object sanitizeCredentials(Object obj, Map<String, String> credentialValues) {
    if (obj == null) {
      return null;
    }

    try {
      JsonNode jsonNode = objectMapper.valueToTree(obj);
      JsonNode sanitizedNode = sanitizeJsonNodeWithCredentials(jsonNode, credentialValues);
      return objectMapper.treeToValue(sanitizedNode, Object.class);
    } catch (Exception e) {
      log.warn("Failed to sanitize credentials, returning original object: {}", e.getMessage());
      return obj;
    }
  }

  public Object sanitizeCredentials(Object obj) {
    return obj;
  }

  public Map<String, String> convertCredentialJsonToMap(JsonNode credentialJson) {
    Map<String, String> credentialValues = new HashMap<>();

    if (credentialJson != null && credentialJson.isObject()) {
      credentialJson
          .fieldNames()
          .forEachRemaining(
              fieldName -> {
                JsonNode value = credentialJson.get(fieldName);
                if (value.isTextual()) {
                  String credentialValue = value.asText();
                  if (credentialValue != null && !credentialValue.trim().isEmpty()) {
                    credentialValues.put(credentialValue, "**ENCRYPTED**");
                  }
                }
              });
    }

    return credentialValues;
  }

  private JsonNode sanitizeJsonNodeWithCredentials(
      JsonNode node, Map<String, String> credentialValues) {
    if (node == null || node.isNull()) {
      return node;
    }

    if (node.isObject()) {
      return sanitizeObjectNodeWithCredentials(node, credentialValues);
    } else if (node.isArray()) {
      return sanitizeArrayNodeWithCredentials(node, credentialValues);
    } else if (node.isTextual()) {
      String textValue = node.asText();
      if (credentialValues.containsKey(textValue)) {
        return objectMapper.valueToTree(credentialValues.get(textValue));
      }
      for (String credentialValue : credentialValues.keySet()) {
        if (textValue.contains(credentialValue)) {
          return objectMapper.valueToTree(
              textValue.replace(credentialValue, credentialValues.get(credentialValue)));
        }
      }
      return node;
    } else {
      return node;
    }
  }

  private JsonNode sanitizeObjectNodeWithCredentials(
      JsonNode objectNode, Map<String, String> credentialValues) {
    ObjectNode sanitizedNode = objectMapper.createObjectNode();

    objectNode
        .fieldNames()
        .forEachRemaining(
            fieldName -> {
              JsonNode value = objectNode.get(fieldName);
              sanitizedNode.set(
                  fieldName, sanitizeJsonNodeWithCredentials(value, credentialValues));
            });

    return sanitizedNode;
  }

  private JsonNode sanitizeArrayNodeWithCredentials(
      JsonNode arrayNode, Map<String, String> credentialValues) {
    ArrayNode sanitizedArray = objectMapper.createArrayNode();

    for (JsonNode element : arrayNode) {
      sanitizedArray.add(sanitizeJsonNodeWithCredentials(element, credentialValues));
    }

    return sanitizedArray;
  }
}
