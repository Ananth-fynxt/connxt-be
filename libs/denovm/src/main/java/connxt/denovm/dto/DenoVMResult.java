package connxt.denovm.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Result DTO for Deno VM execution */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DenoVMResult {

  private boolean success;
  private Object data;
  private String error;
  private Map<String, Object> meta;

  /** Create a successful result */
  public static DenoVMResult success(Object data) {
    return new DenoVMResult(true, data, null, Map.of());
  }

  /** Create a successful result with metadata */
  public static DenoVMResult success(Object data, Map<String, Object> meta) {
    return new DenoVMResult(true, data, null, meta);
  }

  /** Create an error result */
  public static DenoVMResult error(String error) {
    return new DenoVMResult(false, null, error, Map.of());
  }

  /** Create an error result with metadata */
  public static DenoVMResult error(String error, Map<String, Object> meta) {
    return new DenoVMResult(false, null, error, meta);
  }
}
