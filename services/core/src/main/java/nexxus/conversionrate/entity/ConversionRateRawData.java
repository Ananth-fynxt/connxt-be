package nexxus.conversionrate.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import nexxus.shared.db.AuditingEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "conversion_rate_raw_data")
@Builder
public class ConversionRateRawData extends AuditingEntity {

  @EmbeddedId private EmbeddableConversionRateRawDataId rawDataId;

  @Column(name = "source_currency")
  private String sourceCurrency;

  @Column(name = "target_currency")
  private String targetCurrency;

  @Column(name = "time_range")
  private LocalDateTime timeRange;

  @Column(name = "amount", precision = 20, scale = 8)
  private BigDecimal amount;
}
