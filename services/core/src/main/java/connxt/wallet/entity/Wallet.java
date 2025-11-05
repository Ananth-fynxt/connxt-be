package connxt.wallet.entity;

import java.math.BigDecimal;

import connxt.shared.db.AuditingEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "wallet")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wallet extends AuditingEntity {

  @Id @WalletId private String id;

  @Column(name = "brand_id", nullable = false)
  private String brandId;

  @Column(name = "environment_id", nullable = false)
  private String environmentId;

  @Column(name = "brand_customer_id", nullable = false)
  private String brandCustomerId;

  @Column(name = "name")
  private String name;

  @Column(name = "currency", nullable = false)
  private String currency;

  @Column(name = "balance", nullable = false, precision = 20, scale = 8)
  @Builder.Default
  private BigDecimal balance = BigDecimal.ZERO;

  @Column(name = "available_balance", nullable = false, precision = 20, scale = 8)
  @Builder.Default
  private BigDecimal availableBalance = BigDecimal.ZERO;

  @Column(name = "hold_balance", nullable = false, precision = 20, scale = 8)
  @Builder.Default
  private BigDecimal holdBalance = BigDecimal.ZERO;
}
