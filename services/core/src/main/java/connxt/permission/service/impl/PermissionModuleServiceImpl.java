package connxt.permission.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import connxt.permission.service.PermissionModuleService;
import connxt.shared.constants.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionModuleServiceImpl implements PermissionModuleService {

  private final ObjectMapper objectMapper;

  @Override
  public Map<String, Object> getAvailableModules() {
    log.debug("Fetching available permission modules from permissions.json");

    try {
      ClassPathResource resource = new ClassPathResource("permissions.json");
      InputStream inputStream = resource.getInputStream();

      @SuppressWarnings("unchecked")
      Map<String, Object> permissionsData = objectMapper.readValue(inputStream, Map.class);

      @SuppressWarnings("unchecked")
      Map<String, Object> availableModules =
          (Map<String, Object>) permissionsData.get("available_modules");

      if (availableModules == null || availableModules.isEmpty()) {
        log.error("No available modules found in permissions.json");
        throw new ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INVALID_PERMISSION_CONFIGURATION.getCode());
      }

      log.info("Successfully fetched {} available modules", availableModules.size());
      return availableModules;

    } catch (IOException e) {
      log.error("Error reading permissions.json file", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.GENERIC_ERROR.getCode());
    }
  }
}
