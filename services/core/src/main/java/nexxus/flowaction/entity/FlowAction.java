package nexxus.flowaction.entity;

import java.util.List;

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
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "flow_actions")
public class FlowAction extends AuditingEntity {

  @Id @FlowActionId private String id;

  private String name;

  @Column(name = "steps")
  private List<String> steps;

  @Column(name = "flow_type_id")
  private String flowTypeId;

  @Type(JsonBinaryType.class)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "input_schema", columnDefinition = "jsonb")
  private JsonNode inputSchema;

  @Type(JsonBinaryType.class)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "output_schema", columnDefinition = "jsonb")
  private JsonNode outputSchema;
}
