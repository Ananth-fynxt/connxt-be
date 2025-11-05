package connxt.conversionrate.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.Type;

import connxt.shared.constants.Status;
import connxt.shared.db.AuditingEntity;
import connxt.shared.db.PostgreSQLEnumType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "conversion_rate")
@Builder
public class ConversionRate extends AuditingEntity {

  @EmbeddedId private EmbeddableConversionRateId rateId;

  @Column(name = "brand_id")
  private String brandId;

  @Column(name = "environment_id")
  private String environmentId;

  @Type(
      value = PostgreSQLEnumType.class,
      parameters =
          @org.hibernate.annotations.Parameter(
              name = "enumClass",
              value = "connxt.shared.constants.Status"))
  @Enumerated(EnumType.STRING)
  @Column(name = "status", columnDefinition = "status")
  @Builder.Default
  private Status status = Status.ENABLED;

  @Column(name = "source_currency")
  private String sourceCurrency;

  @Column(name = "target_currency")
  private String targetCurrency;

  @Column(name = "value", precision = 20, scale = 8)
  private BigDecimal value;
}
