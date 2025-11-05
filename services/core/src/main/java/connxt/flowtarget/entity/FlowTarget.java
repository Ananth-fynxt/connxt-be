package connxt.flowtarget.entity;

import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import connxt.shared.constants.Status;
import connxt.shared.db.AuditingEntity;
import connxt.shared.db.PostgreSQLEnumType;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "flow_targets")
public class FlowTarget extends AuditingEntity {
  @Id @FlowTargetId private String id;

  private String name;

  private String logo;

  @Type(
      value = PostgreSQLEnumType.class,
      parameters =
          @org.hibernate.annotations.Parameter(
              name = "enumClass",
              value = "connxt.shared.constants.Status"))
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, columnDefinition = "status")
  @Builder.Default
  private Status status = Status.ENABLED;

  @Type(JsonBinaryType.class)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "credential_schema", nullable = false, columnDefinition = "jsonb")
  private JsonNode credentialSchema;

  @Type(JsonBinaryType.class)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "input_schema", nullable = false, columnDefinition = "jsonb")
  @Builder.Default
  private JsonNode inputSchema = null;

  @Column(name = "currencies", nullable = false)
  @Builder.Default
  private List<String> currencies = List.of();

  @Column(name = "countries", nullable = false)
  @Builder.Default
  private List<String> countries = List.of();

  @Column(name = "payment_methods", nullable = false)
  @Builder.Default
  private List<String> paymentMethods = List.of();

  @Column(name = "flow_type_id")
  private String flowTypeId;
}
