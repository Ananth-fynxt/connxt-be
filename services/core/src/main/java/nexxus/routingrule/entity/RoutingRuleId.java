package nexxus.routingrule.entity;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.hibernate.annotations.IdGeneratorType;

@IdGeneratorType(RoutingRuleIdGenerator.class)
@Retention(RUNTIME)
@Target({FIELD, METHOD})
public @interface RoutingRuleId {}
