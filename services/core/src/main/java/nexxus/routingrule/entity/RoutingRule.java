package nexxus.routingrule.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import nexxus.shared.constants.PspSelectionMode;
import nexxus.shared.constants.RoutingDuration;
import nexxus.shared.constants.RoutingType;
import nexxus.shared.constants.Status;
import nexxus.shared.db.AuditingEntity;
import nexxus.shared.db.PostgreSQLEnumType;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "routing_rules")
public class RoutingRule extends AuditingEntity {

  @EmbeddedId private EmbeddableRoutingRuleId routingRuleId;

  @Column(name = "name")
  private String name;

  @Column(name = "brand_id")
  private String brandId;

  @Column(name = "environment_id")
  private String environmentId;

  @Enumerated(EnumType.STRING)
  @Type(
      value = PostgreSQLEnumType.class,
      parameters =
          @org.hibernate.annotations.Parameter(
              name = "enumClass",
              value = "nexxus.shared.constants.PspSelectionMode"))
  @Column(name = "psp_selection_mode", columnDefinition = "psp_selection_mode")
  private PspSelectionMode pspSelectionMode;

  @Enumerated(EnumType.STRING)
  @Type(
      value = PostgreSQLEnumType.class,
      parameters =
          @org.hibernate.annotations.Parameter(
              name = "enumClass",
              value = "nexxus.shared.constants.RoutingType"))
  @Column(name = "routing_type", columnDefinition = "routing_type")
  private RoutingType routingType;

  @Enumerated(EnumType.STRING)
  @Type(
      value = PostgreSQLEnumType.class,
      parameters =
          @org.hibernate.annotations.Parameter(
              name = "enumClass",
              value = "nexxus.shared.constants.RoutingDuration"))
  @Column(name = "duration", columnDefinition = "routing_duration")
  private RoutingDuration duration;

  @Type(JsonBinaryType.class)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "condition_json", columnDefinition = "jsonb")
  private JsonNode conditionJson;

  @Enumerated(EnumType.STRING)
  @Type(
      value = PostgreSQLEnumType.class,
      parameters =
          @org.hibernate.annotations.Parameter(
              name = "enumClass",
              value = "nexxus.shared.constants.Status"))
  @Column(name = "status", columnDefinition = "status")
  private Status status;
}
