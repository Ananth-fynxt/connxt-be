package connxt.auth.dto;

import java.time.OffsetDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

  private String accessToken;
  private String refreshToken;
  private String tokenType;
  private OffsetDateTime issuedAt;
  private OffsetDateTime expiresAt;
  private Map<String, Object> claims;
}
