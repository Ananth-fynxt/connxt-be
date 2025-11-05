package nexxus.brandrole.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import nexxus.shared.db.AuditingEntity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "brand_roles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandRole extends AuditingEntity {

  @Id @BrandRoleId private String id;

  @Column(name = "brand_id", nullable = false)
  private String brandId;

  @Column(name = "environment_id", nullable = false)
  private String environmentId;

  @Column(name = "name", nullable = false)
  private String name;

  @Type(JsonBinaryType.class)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "permission", columnDefinition = "jsonb")
  private JsonNode permission;
}
