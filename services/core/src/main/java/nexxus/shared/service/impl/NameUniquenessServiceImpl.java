package nexxus.shared.service.impl;

import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import nexxus.shared.service.NameUniquenessService;

@Service
public class NameUniquenessServiceImpl implements NameUniquenessService {

  @Override
  public void validateForCreate(
      Function<String, Boolean> nameExistsFunction, String entityName, String name) {
    if (nameExistsFunction.apply(name)) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, String.format("%s already exists with the given name", entityName));
    }
  }

  @Override
  public void validateForUpdateWithFlowContext(
      String existingName,
      String newName,
      String brandId,
      String environmentId,
      String flowActionId,
      NameExistsWithFlowContextFunction nameExistsFunction,
      String entityName) {

    if (!existingName.equals(newName)) {
      if (nameExistsFunction.exists(brandId, environmentId, flowActionId, newName)) {
        throw new ResponseStatusException(
            HttpStatus.CONFLICT,
            String.format("%s already exists with the given name", entityName));
      }
    }
  }
}
