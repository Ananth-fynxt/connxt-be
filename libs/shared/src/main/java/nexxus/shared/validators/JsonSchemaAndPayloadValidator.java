package nexxus.shared.validators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import lombok.extern.slf4j.Slf4j;

/** Schema validation utility for validating JSON payloads against JSON schemas */
@Slf4j
@Component
public class JsonSchemaAndPayloadValidator {

  private final ObjectMapper objectMapper;
  private final JsonSchemaFactory schemaFactory;

  @Autowired
  public JsonSchemaAndPayloadValidator(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    this.schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
  }

  /**
   * Validates a JSON payload against a JSON schema
   *
   * @param schemaJson JSON schema as string
   * @param payload JSON payload as string
   * @return ValidationResult containing validation status and errors
   */
  public ValidationResult validate(String schemaJson, String payload) {
    try {
      JsonNode schemaNode = objectMapper.readTree(schemaJson);
      JsonNode payloadNode = objectMapper.readTree(payload);

      JsonSchema schema = schemaFactory.getSchema(schemaNode);
      Set<ValidationMessage> validationMessages = schema.validate(payloadNode);

      if (validationMessages.isEmpty()) {
        return ValidationResult.success();
      } else {
        List<String> errors = new ArrayList<>();
        for (ValidationMessage message : validationMessages) {
          errors.add(message.getMessage());
        }
        return ValidationResult.failure(errors);
      }
    } catch (IOException e) {
      return ValidationResult.failure(List.of("Invalid JSON format: " + e.getMessage()));
    } catch (Exception e) {
      return ValidationResult.failure(List.of("Invalid JSON schema: " + e.getMessage()));
    }
  }

  /**
   * Validates a JSON payload against a JSON schema and throws exception if validation fails
   *
   * @param schemaJson JSON schema as string
   * @param payload JSON payload as string
   * @throws SchemaValidationException if validation fails
   */
  public void validateAndThrow(String schemaJson, String payload) throws SchemaValidationException {
    ValidationResult result = validate(schemaJson, payload);
    if (!result.valid()) {
      throw new SchemaValidationException("Schema validation failed", result.errors());
    }
  }
}
