package nexxus.denovm.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Execution context for Deno VM execution */
@Data
@AllArgsConstructor
public class DenoVMExecutionContext {
  public final String id;
  public final String file;
  public final Map<String, String> credential;
  public final Object data;
  public final String step;
  public final DenoVMRequest.DenoVMUrls urls;
}
