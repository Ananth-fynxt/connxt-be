package nexxus.shared.context;

import java.util.List;
import java.util.Map;

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
  private String roleId;
  private String userId;
  private String scope;
  private String authType;
  private String fiId;
  private String customerId;
  private List<String> accessibleBrandIds;
  private Map<String, Object> rolePermissions;
}
