package connxt.shared.filter.strategy;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import connxt.shared.config.RouteConfig;
import connxt.shared.constants.ErrorCode;
import connxt.shared.context.BrandEnvironmentContext;
import connxt.shared.context.BrandEnvironmentContextHolder;
import connxt.shared.service.RolePermissionEnrichmentService;
import connxt.shared.util.ErrorResponseUtil;
import connxt.shared.validators.DynamicClaimParserValidator;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class HeaderRequiredRouteStrategy implements RouteFilterStrategy {

  private static final String HEADER_BRAND_ID = "X-BRAND-ID";
  private static final String HEADER_ENVIRONMENT_ID = "X-ENV-ID";

  private final RouteConfig routeConfig;
  private final DynamicClaimParserValidator claimParserValidator;
  private final RolePermissionEnrichmentService rolePermissionEnrichmentService;

  @Override
  public boolean canHandle(String requestUri) {
    return routeConfig.requiresBrandEnvHeaders(requestUri);
  }

  @Override
  public boolean process(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException {
    try {
      if (!validateRequiredHeaders(request, response)) {
        return false;
      }

      String brandId = request.getHeader(HEADER_BRAND_ID);
      String environmentId = request.getHeader(HEADER_ENVIRONMENT_ID);
      String scope = BrandEnvironmentContextHolder.getScope();

      @SuppressWarnings("unchecked")
      Map<String, Object> jwtClaims = (Map<String, Object>) request.getAttribute("jwt.claims");
      String userId = (String) request.getAttribute("jwt.subject");

      if (jwtClaims == null || userId == null) {
        log.warn(
            "JWT claims not found in request attributes for header-required route: {}",
            request.getRequestURI());
        return false;
      }

      if (!claimParserValidator.validateBrandEnvironmentInClaims(
          jwtClaims, scope, brandId, environmentId)) {
        ErrorResponseUtil.writeErrorResponse(
            request, response, ErrorCode.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        return false;
      }

      String roleId =
          claimParserValidator.extractRoleIdFromClaims(jwtClaims, scope, brandId, environmentId);

      updateContextWithBrandEnvironment(brandId, environmentId, roleId);

      if (roleId != null && !roleId.trim().isEmpty()) {
        try {
          rolePermissionEnrichmentService.enrichContextWithRolePermissions();
        } catch (Exception e) {
        }
      }

      filterChain.doFilter(request, response);

      return false;
    } catch (Exception e) {
      throw new ServletException("Error processing header-required route", e);
    } finally {
      BrandEnvironmentContextHolder.clear();
    }
  }

  @Override
  public String getStrategyName() {
    return "HeaderRequiredRouteStrategy";
  }

  private boolean validateRequiredHeaders(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    String brandId = request.getHeader(HEADER_BRAND_ID);
    String environmentId = request.getHeader(HEADER_ENVIRONMENT_ID);

    if (StringUtils.isBlank(brandId) || StringUtils.isBlank(environmentId)) {
      log.warn(
          "Missing required headers for request: {} - X-BRAND-ID: {}, X-ENV-ID: {}",
          request.getRequestURI(),
          brandId,
          environmentId);
      ErrorResponseUtil.writeErrorResponse(
          request, response, ErrorCode.MISSING_REQUIRED_HEADERS, HttpStatus.BAD_REQUEST);
      return false;
    }

    return true;
  }

  private void updateContextWithBrandEnvironment(
      String brandId, String environmentId, String roleId) {
    BrandEnvironmentContext existingContext = BrandEnvironmentContextHolder.getContext();

    if (existingContext == null) {
      log.warn("No existing context found when updating with brand/environment data");
      return;
    }

    BrandEnvironmentContext updatedContext =
        existingContext.toBuilder()
            .brandId(brandId)
            .environmentId(environmentId)
            .roleId(roleId)
            .build();

    BrandEnvironmentContextHolder.setContext(updatedContext);

    log.debug(
        "Brand-Environment context updated: brandId={}, environmentId={}, roleId={}, scope={}",
        brandId,
        environmentId,
        roleId,
        existingContext.getScope());
  }
}
