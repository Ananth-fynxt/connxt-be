package connxt.permission.services.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import connxt.permission.services.PermissionService;
import connxt.shared.exception.PermissionDeniedException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {

  @Override
  public boolean hasPermission(String module, String action) {
    String scope = getScopeFromRequest();

    log.debug("Permission check - scope: {}, module: {}, action: {}", scope, module, action);

    // SYSTEM scope doesn't need permission validation
    if ("SYSTEM".equals(scope)) {
      return true;
    }

    // BRAND scope needs role permission validation
    if ("BRAND".equals(scope)) {
      return validateBrandPermission(module, action);
    }

    return false;
  }

  @Override
  public void requirePermission(String module, String action) {
    if (!hasPermission(module, action)) {
      throw new PermissionDeniedException(
          String.format("You don't have permission to '%s' on '%s' module", action, module));
    }
  }

  private boolean validateBrandPermission(String module, String action) {
    Map<String, Object> rolePermissions = getRolePermissionsFromRequest();

    if (rolePermissions == null) {
      log.debug("No role permissions found in context");
      return false;
    }

    if (!rolePermissions.containsKey(module)) {
      log.debug("Module '{}' not found in role permissions", module);
      return false;
    }

    Object moduleObj = rolePermissions.get(module);
    if (!(moduleObj instanceof Map)) {
      log.debug("Invalid module permissions format for module: {}", module);
      return false;
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> modulePermissions = (Map<String, Object>) moduleObj;

    if (!modulePermissions.containsKey("actions")) {
      log.debug("No actions defined for module '{}'", module);
      return false;
    }

    Object actionsObj = modulePermissions.get("actions");
    if (!(actionsObj instanceof List)) {
      log.debug("Invalid actions format for module: {}", module);
      return false;
    }

    @SuppressWarnings("unchecked")
    List<String> actions = (List<String>) actionsObj;

    return actions.contains(action);
  }

  private String getScopeFromRequest() {
    try {
      RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
      if (requestAttributes instanceof ServletRequestAttributes) {
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        @SuppressWarnings("unchecked")
        Map<String, Object> claims = (Map<String, Object>) request.getAttribute("jwt.claims");
        if (claims != null) {
          return (String) claims.get("scope");
        }
      }
    } catch (Exception e) {
      log.debug("Error extracting scope from request: {}", e.getMessage());
    }
    return null;
  }

  private Map<String, Object> getRolePermissionsFromRequest() {
    try {
      RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
      if (requestAttributes instanceof ServletRequestAttributes) {
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        @SuppressWarnings("unchecked")
        Map<String, Object> claims = (Map<String, Object>) request.getAttribute("jwt.claims");
        if (claims != null) {
          @SuppressWarnings("unchecked")
          Map<String, Object> rolePermissions =
              (Map<String, Object>) claims.get("role_permissions");
          return rolePermissions;
        }
      }
    } catch (Exception e) {
      log.debug("Error extracting role permissions from request: {}", e.getMessage());
    }
    return null;
  }
}
