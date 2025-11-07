package connxt.permission.aspects;

import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import connxt.permission.annotations.RequiresScope;
import connxt.shared.exception.PermissionDeniedException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@Order(1)
public class ScopeCheckAspect {

  @Around("@within(connxt.permission.annotations.RequiresScope)")
  public Object checkScope(ProceedingJoinPoint joinPoint) throws Throwable {

    Class<?> declaringType = joinPoint.getSignature().getDeclaringType();
    RequiresScope annotation = declaringType.getAnnotation(RequiresScope.class);

    if (annotation == null) {
      return joinPoint.proceed();
    }

    String[] allowedScopes = annotation.value();
    String userScope = getScopeFromRequest();

    if (userScope == null) {
      throw new PermissionDeniedException("Authentication required - no scope found");
    }

    boolean scopeAllowed = false;
    for (String allowedScope : allowedScopes) {
      if (allowedScope.equals(userScope)) {
        scopeAllowed = true;
        break;
      }
    }

    if (!scopeAllowed) {
      throw new PermissionDeniedException("Access denied - scope '" + userScope + "' not allowed");
    }

    return joinPoint.proceed();
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
      // Ignore - will return null
    }
    return null;
  }
}
