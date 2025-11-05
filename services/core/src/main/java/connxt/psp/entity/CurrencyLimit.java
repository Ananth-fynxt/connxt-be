package connxt.psp.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "currency_limits")
@IdClass(CurrencyLimitId.class)
public class CurrencyLimit {

  @Id
  @Column(name = "brand_id")
  private String brandId;

  @Id
  @Column(name = "environment_id")
  private String environmentId;

  @Id
  @Column(name = "flow_action_id")
  private String flowActionId;

  @Id
  @Column(name = "psp_id")
  private String pspId;

  @Id
  @Column(name = "currency")
  private String currency;

  @Column(name = "min_value")
  private BigDecimal minValue;

  @Column(name = "max_value")
  private BigDecimal maxValue;
}
