package connxt.shared.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "connxt.routes")
public class RouteConfig {

  private List<String> publicPaths;
  private List<String> systemOnlyPaths;
  private final PathPatternParser parser = new PathPatternParser();

  public boolean isPublic(String requestUri) {
    return matchesAny(requestUri, publicPaths);
  }

  public boolean isJwtRequired(String requestUri) {
    return !isPublic(requestUri);
  }

  public boolean isSystemOnlyRoute(String requestUri) {
    return matchesAny(requestUri, systemOnlyPaths);
  }

  public String[] getPublicPaths() {
    return publicPaths != null ? publicPaths.toArray(new String[0]) : new String[0];
  }

  private boolean matchesAny(String requestUri, List<String> paths) {
    return requestUri != null
        && paths != null
        && paths.stream()
            .anyMatch(
                path -> {
                  try {
                    PathPattern pattern = parser.parse(path);
                    PathContainer pathContainer = PathContainer.parsePath(requestUri);
                    return pattern.matches(pathContainer);
                  } catch (Exception e) {
                    return false;
                  }
                });
  }
}
