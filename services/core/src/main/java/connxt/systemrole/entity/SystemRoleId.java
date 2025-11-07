package connxt.systemrole.entity;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.hibernate.annotations.IdGeneratorType;

@Documented
@Target({FIELD})
@Retention(RUNTIME)
@IdGeneratorType(SystemRoleIdGenerator.class)
public @interface SystemRoleId {}
