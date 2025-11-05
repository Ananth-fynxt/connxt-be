package nexxus.psp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "supported_currencies")
@IdClass(SupportedCurrencyId.class)
public class SupportedCurrency {

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
}
