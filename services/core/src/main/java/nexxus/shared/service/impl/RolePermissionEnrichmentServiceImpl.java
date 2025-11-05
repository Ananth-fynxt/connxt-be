package nexxus.shared.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import nexxus.brandrole.service.BrandRoleService;
import nexxus.shared.context.BrandEnvironmentContext;
import nexxus.shared.context.BrandEnvironmentContextHolder;
import nexxus.shared.service.RolePermissionEnrichmentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RolePermissionEnrichmentServiceImpl implements RolePermissionEnrichmentService {

  private final BrandRoleService brandRoleService;

  @Override
  public void enrichContextWithRolePermissions() {
    BrandEnvironmentContext currentContext = BrandEnvironmentContextHolder.getContext();

    if (currentContext == null) {
      return;
    }

    String roleId = currentContext.getRoleId();
    if (roleId == null || roleId.trim().isEmpty()) {
      return;
    }

    try {
      Map<String, Object> rolePermissions = brandRoleService.getRolePermissions(roleId);

      BrandEnvironmentContext enrichedContext =
          BrandEnvironmentContext.builder()
              .brandId(currentContext.getBrandId())
              .environmentId(currentContext.getEnvironmentId())
              .roleId(currentContext.getRoleId())
              .userId(currentContext.getUserId())
              .scope(currentContext.getScope())
              .authType(currentContext.getAuthType())
              .fiId(currentContext.getFiId())
              .accessibleBrandIds(currentContext.getAccessibleBrandIds())
              .customerId(currentContext.getCustomerId())
              .rolePermissions(rolePermissions)
              .build();

      BrandEnvironmentContextHolder.setContext(enrichedContext);
    } catch (Exception e) {
      log.warn(
          "Failed to enrich context with role permissions for roleId: {}, error: {}",
          roleId,
          e.getMessage());
    }
  }
}
