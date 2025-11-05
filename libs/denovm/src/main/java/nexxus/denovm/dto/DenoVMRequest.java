package nexxus.denovm.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request DTO for Deno VM execution */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DenoVMRequest {

  private String id;
  private String code;
  private Map<String, String> credential;
  private Map<String, Object> data;
  private DenoVMUrls urls;
  private String step;

  /** URLs configuration for VM execution */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DenoVMUrls {
    private DenoVMUrl server;
  }

  /** URL configuration for server or origin */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DenoVMUrl {
    private String redirect;
    private String webhook;
    private String successRedirectUrl;
    private String failureRedirectUrl;
  }
}
