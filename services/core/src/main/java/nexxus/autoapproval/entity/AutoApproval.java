package nexxus.autoapproval.entity;

import java.math.BigDecimal;

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
@Table(name = "auto_approval")
@Builder
public class AutoApproval extends AuditingEntity {

  @EmbeddedId private EmbeddableAutoApprovalId autoApprovalId;

  @Column(name = "name")
  private String name;

  @Column(name = "currency")
  private String currency;

  @Column(name = "countries", columnDefinition = "TEXT[]")
  private String[] countries;

  @Column(name = "brand_id")
  private String brandId;

  @Column(name = "environment_id")
  private String environmentId;

  @Column(name = "flow_action_id")
  private String flowActionId;

  @Column(name = "max_amount")
  private BigDecimal maxAmount;

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
