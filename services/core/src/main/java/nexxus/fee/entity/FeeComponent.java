package nexxus.fee.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.Type;

import nexxus.shared.constants.FeeComponentType;
import nexxus.shared.db.PostgreSQLEnumType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fee_components")
public class FeeComponent {

  @EmbeddedId private EmbeddableFeeComponentId feeComponentId;

  @Type(
      value = PostgreSQLEnumType.class,
      parameters =
          @org.hibernate.annotations.Parameter(
              name = "enumClass",
              value = "nexxus.shared.constants.FeeComponentType"))
  @Enumerated(EnumType.STRING)
  @Column(name = "fee_component_type", columnDefinition = "fee_component_type")
  private FeeComponentType type;

  @Column(name = "amount")
  private BigDecimal amount;

  @Column(name = "min_value")
  private BigDecimal minValue;

  @Column(name = "max_value")
  private BigDecimal maxValue;
}
