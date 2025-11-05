package nexxus.session.entity;

import java.time.Instant;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import nexxus.shared.db.AuditingEntity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "widget_sessions")
@Builder
public class WidgetSession extends AuditingEntity {

  @Id @SessionId private String id;

  @Column(name = "brand_id")
  private String brandId;

  @Column(name = "environment_id")
  private String environmentId;

  @Column(name = "customer_id")
  private String customerId;

  @Column(name = "session_token_hash", length = 512)
  private String sessionTokenHash;

  @Column(name = "fingerprint_hash", length = 512)
  private String fingerprintHash;

  @Column(name = "expires_at")
  private Instant expiresAt;

  @Column(name = "last_refreshed_at")
  private Instant lastRefreshedAt;

  @Column(name = "last_accessed_at")
  private Instant lastAccessedAt;

  @Column(name = "extension_count")
  @Builder.Default
  private Integer extensionCount = 0;

  @Column(name = "max_extensions")
  @Builder.Default
  private Integer maxExtensions = 3;

  @Column(name = "timeout_minutes")
  @Builder.Default
  private Integer timeoutMinutes = 60;

  @Column(name = "auto_extend")
  @Builder.Default
  private Boolean autoExtend = true;

  @Column(name = "revoked")
  @Builder.Default
  private Boolean revoked = false;

  @Column(name = "revoked_by")
  private String revokedBy;

  @Column(name = "revoked_at")
  private Instant revokedAt;

  @Type(JsonBinaryType.class)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "fingerprint", columnDefinition = "jsonb")
  private JsonNode fingerprint;
}
