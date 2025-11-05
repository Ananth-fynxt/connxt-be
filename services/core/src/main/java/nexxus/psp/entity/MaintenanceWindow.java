package nexxus.psp.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import nexxus.shared.constants.Status;
import nexxus.shared.db.AuditingEntity;
import nexxus.shared.db.PostgreSQLEnumType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "maintenance_windows")
@Audited
public class MaintenanceWindow extends AuditingEntity {

  @Id @MaintenanceWindowId private String id;

  @Column(name = "psp_id")
  private String pspId;

  @Column(name = "flow_action_id")
  private String flowActionId;

  @Column(name = "start_at")
  private LocalDateTime startAt;

  @Column(name = "end_at")
  private LocalDateTime endAt;

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
}
