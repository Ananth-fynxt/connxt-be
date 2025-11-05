package nexxus.jwt.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtValidationResponse {

  private boolean valid;
  private String errorMessage;
  private String subject;
  private String issuer;
  private String audience;
  private Map<String, Object> claims;
}
