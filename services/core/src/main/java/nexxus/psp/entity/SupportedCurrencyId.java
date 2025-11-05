package nexxus.psp.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SupportedCurrencyId implements Serializable {
  public String brandId;
  public String environmentId;
  public String flowActionId;
  public String pspId;
  public String currency;
}
