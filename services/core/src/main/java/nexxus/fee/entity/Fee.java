package nexxus.fee.entity;

import org.hibernate.annotations.Type;

import nexxus.shared.constants.ChargeFeeType;
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
@Table(name = "fee")
@Builder
public class Fee extends AuditingEntity {

  @EmbeddedId private EmbeddableFeeId feeId;

  @Column(name = "name")
  private String name;

  @Column(name = "currency")
  private String currency;

  @Column(name = "countries", columnDefinition = "TEXT[]")
  private String[] countries;

  @Type(
      value = PostgreSQLEnumType.class,
      parameters =
          @org.hibernate.annotations.Parameter(
              name = "enumClass",
              value = "nexxus.shared.constants.ChargeFeeType"))
  @Enumerated(EnumType.STRING)
  @Column(name = "charge_fee_type", columnDefinition = "charge_fee_type")
  private ChargeFeeType chargeFeeType;

  @Column(name = "brand_id")
  private String brandId;

  @Column(name = "environment_id")
  private String environmentId;

  @Column(name = "flow_action_id")
  private String flowActionId;

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
