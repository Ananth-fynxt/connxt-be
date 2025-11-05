package connxt.shared.exception;

import org.jspecify.annotations.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public interface ErrorDetail {
  @Nullable String object();

  @Nullable String field();

  String message();

  @Nullable Object rejectedValue();
}
