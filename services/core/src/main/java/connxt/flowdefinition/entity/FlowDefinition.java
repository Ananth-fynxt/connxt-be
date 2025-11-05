package connxt.flowdefinition.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import connxt.shared.db.AuditingEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "flow_definitions")
public class FlowDefinition extends AuditingEntity {
  @Id @FlowDefinitionId private String id;

  @Column(name = "flow_action_id")
  private String flowActionId;

  @Column(name = "flow_target_id")
  private String flowTargetId;

  private String description;

  private String code;

  @Column(name = "brand_id")
  private String brandId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "flow_configuration", columnDefinition = "jsonb")
  private JsonNode flowConfiguration;
}
