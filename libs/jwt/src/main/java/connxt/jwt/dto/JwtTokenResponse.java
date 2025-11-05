package connxt.jwt.dto;

import java.time.OffsetDateTime;
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
public class JwtTokenResponse {

  private String token;
  private String refreshToken;
  private String tokenType;
  private OffsetDateTime issuedAt;
  private OffsetDateTime expiresAt;
  private Map<String, Object> claims;
}
