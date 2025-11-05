package connxt.environment.entity;

import connxt.shared.db.AuditingEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "environments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Environment extends AuditingEntity {

  @Id @EnvironmentId private String id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "secret", nullable = false, unique = true)
  @EnvironmentSecretId
  private String secret;

  @Column(name = "token", nullable = false, unique = true)
  @TokenId
  private String token;

  @Column(name = "origin")
  private String origin;

  @Column(name = "success_redirect_url")
  private String successRedirectUrl;

  @Column(name = "failure_redirect_url")
  private String failureRedirectUrl;

  @Column(name = "brand_id", nullable = false)
  private String brandId;
}
