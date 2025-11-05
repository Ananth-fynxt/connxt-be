package nexxus.pspgroup.entity;

import java.util.List;

import org.hibernate.annotations.Type;

import nexxus.shared.constants.Status;
import nexxus.shared.db.AuditingEntity;
import nexxus.shared.db.PostgreSQLEnumType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "psp_groups")
@Builder
public class PspGroup extends AuditingEntity {

  @EmbeddedId private EmbeddablePspGroupId pspGroupId;

  @Column(name = "brand_id")
  private String brandId;

  @Column(name = "environment_id")
  private String environmentId;

  @Column(name = "name")
  private String name;

  @Column(name = "flow_action_id")
  private String flowActionId;

  @Column(name = "currency")
  private String currency;

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

  @OneToMany(mappedBy = "pspGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<PspGroupPsp> pspGroupPsps = new java.util.ArrayList<>();
}
