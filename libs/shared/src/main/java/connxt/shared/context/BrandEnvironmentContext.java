package connxt.shared.context;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BrandEnvironmentContext {

  private String brandId;
  private String environmentId;
  private String userId;
  private String scope;
  private String authType;
  private String customerId;
  private List<String> accessibleBrandIds;
}
