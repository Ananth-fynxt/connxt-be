package nexxus.shared.service;

import java.util.function.Function;

public interface NameUniquenessService {

  void validateForCreate(
      Function<String, Boolean> nameExistsFunction, String entityName, String name);

  void validateForUpdateWithFlowContext(
      String existingName,
      String newName,
      String brandId,
      String environmentId,
      String flowActionId,
      NameExistsWithFlowContextFunction nameExistsFunction,
      String entityName);

  @FunctionalInterface
  interface NameExistsWithFlowContextFunction {
    boolean exists(String brandId, String environmentId, String flowActionId, String name);
  }
}
