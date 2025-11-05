package nexxus.jwt.dto;

import java.time.OffsetDateTime;
import java.util.Map;

import nexxus.shared.constants.TokenType;

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
public class JwtTokenRequest {

  private String issuer;
  private String audience;
  private String subject;
  private OffsetDateTime issuedAt;
  private OffsetDateTime expiresAt;
  private Map<String, Object> claims;
  private String signingKeyId;
  private TokenType tokenType;
}
