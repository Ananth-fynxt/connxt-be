package connxt.auth.strategy;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import connxt.auth.service.impl.TokenManagementService;
import connxt.jwt.dto.JwtValidationRequest;
import connxt.jwt.dto.JwtValidationResponse;
import connxt.jwt.executor.JwtExecutor;
import connxt.shared.config.RouteConfig;
import connxt.shared.constants.ErrorCode;
import connxt.shared.constants.TokenType;
import connxt.shared.context.BrandEnvironmentContext;
import connxt.shared.context.BrandEnvironmentContextHolder;
import connxt.shared.filter.auth.AuthenticationStrategy;
import connxt.shared.util.ErrorResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationStrategy implements AuthenticationStrategy {

  private final JwtExecutor jwtExecutor;
  private final RouteConfig routeConfig;
  private final TokenManagementService tokenManagementService;

  @Value("${security.jwt.issuer}")
  private String jwtIssuer;

  @Value("${security.jwt.audience}")
  private String jwtAudience;

  @Value("${security.jwt.signing-key-id}")
  private String signingKeyId;

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  @Override
  public boolean canHandle(HttpServletRequest request) {
    // Handle requests that require JWT validation (not public paths)
    if (!routeConfig.isJwtRequired(request.getRequestURI())) {
      return false;
    }

    String authHeader = request.getHeader(AUTHORIZATION_HEADER);
    return StringUtils.isNotBlank(authHeader) && authHeader.startsWith(BEARER_PREFIX);
  }

  @Override
  public boolean validate(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    // Double-check that JWT is required for this path
    if (!routeConfig.isJwtRequired(request.getRequestURI())) {
      return true;
    }

    String authHeader = request.getHeader(AUTHORIZATION_HEADER);

    if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
      ErrorResponseUtil.writeErrorResponse(
          request, response, ErrorCode.AUTHENTICATION_REQUIRED, HttpStatus.UNAUTHORIZED);
      return false;
    }

    String token = authHeader.substring(BEARER_PREFIX.length()).trim();
    if (token.isEmpty()) {
      ErrorResponseUtil.writeErrorResponse(
          request, response, ErrorCode.AUTHENTICATION_REQUIRED, HttpStatus.UNAUTHORIZED);
      return false;
    }

    try {
      JwtValidationRequest validationRequest =
          JwtValidationRequest.builder()
              .token(token)
              .issuer(jwtIssuer)
              .audience(jwtAudience)
              .signingKeyId(signingKeyId)
              .build();

      JwtValidationResponse validationResult = jwtExecutor.validateToken(validationRequest);

      if (!validationResult.isValid()) {
        ErrorResponseUtil.writeErrorResponse(
            request, response, ErrorCode.TOKEN_VALIDATION_FAILED, HttpStatus.UNAUTHORIZED);
        return false;
      }

      // Verify this is an ACCESS token (not a refresh token)
      String tokenType = (String) validationResult.getClaims().get("token_type");
      if (!TokenType.ACCESS.getValue().equals(tokenType)) {
        ErrorResponseUtil.writeErrorResponse(
            request, response, ErrorCode.TOKEN_VALIDATION_FAILED, HttpStatus.UNAUTHORIZED);
        return false;
      }

      // Additional security check: Verify token is not revoked in database
      String subject = validationResult.getSubject();
      if (!isAccessTokenActiveInDatabase(token, subject)) {
        ErrorResponseUtil.writeErrorResponse(
            request, response, ErrorCode.TOKEN_VALIDATION_FAILED, HttpStatus.UNAUTHORIZED);
        return false;
      }

      // Set user context in request attributes for downstream processing
      request.setAttribute("jwt.subject", validationResult.getSubject());
      request.setAttribute("jwt.claims", validationResult.getClaims());
      request.setAttribute("jwt.token", token);

      // Set Spring Security context to indicate user is authenticated
      setSpringSecurityContext(validationResult.getClaims(), validationResult.getSubject());

      // Set BrandEnvironmentContext for scope validation
      setBrandEnvironmentContext(validationResult.getClaims(), validationResult.getSubject());

      return true;
    } catch (Exception e) {
      ErrorResponseUtil.writeErrorResponse(
          request, response, ErrorCode.TOKEN_VALIDATION_FAILED, HttpStatus.UNAUTHORIZED);
      return false;
    }
  }

  private void setSpringSecurityContext(Map<String, Object> claims, String subject) {
    // Set minimal authentication context at JWT validation stage
    // The BrandEnvironmentContextFilter will update this with proper scope-based authorities
    List<SimpleGrantedAuthority> authorities =
        List.of(new SimpleGrantedAuthority("ROLE_AUTHENTICATED"));

    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(subject, null, authorities);

    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  private void setBrandEnvironmentContext(Map<String, Object> claims, String subject) {
    // Extract only common claims that are available in all JWT tokens
    String scope = (String) claims.get("scope");
    String authType = (String) claims.get("auth_type");
    String customerId = (String) claims.get("customer_id");

    // Set minimal context with only common claims for scope validation
    // brandId and environmentId will be set later by HeaderRequiredRouteStrategy if needed
    BrandEnvironmentContext context =
        BrandEnvironmentContext.builder()
            .userId(subject)
            .scope(scope)
            .authType(authType)
            .customerId(customerId)
            .build();

    BrandEnvironmentContextHolder.setContext(context);
  }

  private boolean isAccessTokenActiveInDatabase(String token, String subject) {
    try {
      String tokenHash = tokenManagementService.generateTokenHashFromToken(token, subject);

      if (tokenHash == null) {
        return false;
      }

      boolean isActive = tokenManagementService.isAccessTokenActive(tokenHash);

      if (!isActive) {
        tokenManagementService.updateExpiredTokenStatus(tokenHash);
      }

      return isActive;
    } catch (Exception e) {
      return false;
    }
  }
}
