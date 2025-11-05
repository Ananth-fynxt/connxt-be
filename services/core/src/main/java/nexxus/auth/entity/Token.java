package nexxus.auth.entity;

import java.time.OffsetDateTime;

import org.hibernate.annotations.Type;

import nexxus.shared.constants.TokenStatus;
import nexxus.shared.constants.TokenType;
import nexxus.shared.db.AuditingEntity;
import nexxus.shared.db.PostgreSQLEnumType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Token extends AuditingEntity {

  @Id @TokenId private String id;

  @Column(name = "customer_id", nullable = false)
  private String customerId;

  @Column(name = "token_hash", nullable = false)
  private String tokenHash;

  @Column(name = "issued_at", nullable = false)
  private OffsetDateTime issuedAt;

  @Column(name = "expires_at", nullable = false)
  private OffsetDateTime expiresAt;

  @Type(
      value = PostgreSQLEnumType.class,
      parameters =
          @org.hibernate.annotations.Parameter(
              name = "enumClass",
              value = "nexxus.shared.constants.TokenStatus"))
  @Enumerated(EnumType.STRING)
  @Column(name = "status", columnDefinition = "status")
  @Builder.Default
  private TokenStatus status = TokenStatus.ACTIVE;

  @Type(
      value = PostgreSQLEnumType.class,
      parameters =
          @org.hibernate.annotations.Parameter(
              name = "enumClass",
              value = "nexxus.shared.constants.TokenType"))
  @Enumerated(EnumType.STRING)
  @Column(name = "token_type", columnDefinition = "token_type")
  private TokenType tokenType;
}
