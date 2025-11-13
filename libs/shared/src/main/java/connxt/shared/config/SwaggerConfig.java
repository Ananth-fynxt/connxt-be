package connxt.shared.config;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

  @Value("${swagger.enabled}")
  private boolean swaggerEnabled;

  @Value("${spring.application.name}")
  private String applicationName;

  @Value("${api.swagger.server-url}")
  private String serverUrls;

  @Bean
  public OpenAPI customOpenAPI() {
    if (!swaggerEnabled) {
      return new OpenAPI()
          .info(
              new Info()
                  .title(applicationName)
                  .description("API documentation is currently disabled"));
    }

    return new OpenAPI()
        .info(createInfo())
        .servers(createServers())
        .components(createComponents())
        .addSecurityItem(createSecurityRequirement());
  }

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("public")
        .pathsToMatch("/api/v1/**")
        .packagesToScan("connxt")
        .build();
  }

  private Info createInfo() {
    return new Info().title(applicationName);
  }

  private List<Server> createServers() {
    return List.of(serverUrls.split(",")).stream()
        .map(String::trim)
        .map(url -> new Server().url(url))
        .toList();
  }

  private Components createComponents() {
    return new Components().addSecuritySchemes("bearerAuth", createBearerAuth());
  }

  private SecurityScheme createBearerAuth() {
    return new SecurityScheme()
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")
        .description(
            "JWT Authorization header using the Bearer scheme. Example: \"Authorization: Bearer {token}\"");
  }

  private SecurityRequirement createSecurityRequirement() {
    return new SecurityRequirement().addList("bearerAuth");
  }
}
