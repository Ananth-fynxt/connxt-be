package connxt.auth.dto;

import connxt.shared.constants.Scope;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInfo {

  private String customerId;
  private String customerName;
  private String customerEmail;
  private String brandId;
  private String brandName;
  private String environmentId;
  private String environmentName;
  private String authType;
  private Scope scope;
}
