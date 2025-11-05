package connxt.request.entity;

import java.math.BigDecimal;

import connxt.shared.db.AuditingEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "request_psps")
@EqualsAndHashCode(callSuper = true)
@IdClass(RequestPspId.class)
public class RequestPsp extends AuditingEntity {

  @Id
  @Column(name = "request_id")
  private String requestId;

  @Id
  @Column(name = "psp_id")
  private String pspId;

  @Column(name = "flow_target_id")
  private String flowTargetId;

  @Column(name = "flow_definition_id")
  private String flowDefinitionId;

  @Column(name = "currency")
  private String currency;

  @Column(name = "original_amount")
  private BigDecimal originalAmount;

  @Column(name = "applied_fee_amount")
  private BigDecimal appliedFeeAmount;

  @Column(name = "total_amount")
  private BigDecimal totalAmount;

  @Column(name = "conversion_from_currency")
  private String conversionFromCurrency;

  @Column(name = "conversion_to_currency")
  private String conversionToCurrency;

  @Column(name = "conversion_exchange_rate")
  private BigDecimal conversionExchangeRate;

  @Column(name = "conversion_converted_amount")
  private BigDecimal conversionConvertedAmount;

  @Column(name = "is_conversion_applied")
  private boolean isConversionApplied;

  @Column(name = "net_amount_to_user")
  private BigDecimal netAmountToUser;

  @Column(name = "inclusive_fee_amount")
  private BigDecimal inclusiveFeeAmount;

  @Column(name = "exclusive_fee_amount")
  private BigDecimal exclusiveFeeAmount;

  @Column(name = "is_fee_applied")
  private boolean isFeeApplied;
}
