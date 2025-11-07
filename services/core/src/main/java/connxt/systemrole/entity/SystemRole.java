package connxt.systemrole.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import connxt.shared.db.AuditingEntity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "system_roles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemRole extends AuditingEntity {

  @Id @SystemRoleId private String id;

  @Column(name = "name", nullable = false)
  private String name;

  @Type(JsonBinaryType.class)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "permissions", columnDefinition = "jsonb", nullable = false)
  private JsonNode permissions;
}
