package connxt.permission.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresScope {

  String[] value();

  boolean exact() default false;

  String errorMessage() default "";

  String errorCode() default "";
}
