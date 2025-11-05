package nexxus.permission.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import nexxus.permission.annotations.RequiresScope;
import nexxus.shared.context.BrandEnvironmentContextHolder;
import nexxus.shared.exception.PermissionDeniedException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@Order(1)
public class ScopeCheckAspect {

  @Around("@within(nexxus.permission.annotations.RequiresScope)")
  public Object checkScope(ProceedingJoinPoint joinPoint) throws Throwable {

    Class<?> declaringType = joinPoint.getSignature().getDeclaringType();
    RequiresScope annotation = declaringType.getAnnotation(RequiresScope.class);

    if (annotation == null) {
      return joinPoint.proceed();
    }

    String[] allowedScopes = annotation.value();
    String userScope = BrandEnvironmentContextHolder.getScope();

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
}
