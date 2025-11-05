package nexxus.psp.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import nexxus.shared.constants.Status;
import nexxus.shared.db.AuditingEntity;
import nexxus.shared.db.PostgreSQLEnumType;

import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "psps")
@Audited
@EqualsAndHashCode(callSuper = true)
public class Psp extends AuditingEntity {
  @Id @PspId private String id;

  @Column(name = "name")
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "logo")
  private String logo;

  @Type(JsonBinaryType.class)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "credential", nullable = false, columnDefinition = "jsonb")
  private JsonNode credential;

  @Column(name = "timeout")
  private Integer timeout;

  @Column(name = "block_vpn_access")
  private Boolean blockVpnAccess;

  @Column(name = "block_data_center_access")
  private Boolean blockDataCenterAccess;

  @Column(name = "failure_rate")
  private Boolean failureRate;

  @Type(StringArrayType.class)
  @Column(name = "ip_address", columnDefinition = "TEXT[]")
  private String[] ipAddress;

  @Column(name = "brand_id")
  private String brandId;

  @Column(name = "environment_id")
  private String environmentId;

  @Column(name = "flow_target_id")
  private String flowTargetId;

  @Type(
      value = PostgreSQLEnumType.class,
      parameters =
          @org.hibernate.annotations.Parameter(
              name = "enumClass",
              value = "nexxus.shared.constants.Status"))
  @Enumerated(EnumType.STRING)
  @Column(name = "status", columnDefinition = "status")
  @Builder.Default
  private Status status = Status.ENABLED;

  @Column(name = "failure_rate_threshold")
  private Float failureRateThreshold;

  @Column(name = "failure_rate_duration_minutes")
  private Integer failureRateDurationMinutes;
}
