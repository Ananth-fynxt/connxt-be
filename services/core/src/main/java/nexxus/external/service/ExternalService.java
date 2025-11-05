package nexxus.external.service;

import java.util.Map;

public interface ExternalService {
  Object read(Map<String, Object> externalDto);

  String extractRedirectUrl(Object result, String token, String tnxId, String step);

  String getEnvironmentOrigin(String tnxId);
}
