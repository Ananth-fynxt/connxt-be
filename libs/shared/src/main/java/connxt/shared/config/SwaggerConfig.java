package connxt.shared.config;

import java.util.List;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
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
        .addOpenApiCustomizer(globalHeadersCustomiser())
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

  public OpenApiCustomizer globalHeadersCustomiser() {
    return openApi -> {
      openApi
          .getPaths()
          .values()
          .forEach(
              pathItem ->
                  pathItem
                      .readOperations()
                      .forEach(
                          operation -> {
                            operation.addParametersItem(createSessionTokenHeader());
                            operation.addParametersItem(createBrandIdHeader());
                            operation.addParametersItem(createEnvironmentIdHeader());
                          }));
    };
  }

  private Parameter createSessionTokenHeader() {
    return new Parameter()
        .name("X-SESSION-TOKEN")
        .in("header")
        .description("Session token for authentication.")
        .required(false)
        .schema(new StringSchema());
  }

  private Parameter createBrandIdHeader() {
    return new Parameter()
        .name("X-BRAND-ID")
        .in("header")
        .description("Brand ID for authentication.")
        .required(false)
        .schema(new StringSchema());
  }

  private Parameter createEnvironmentIdHeader() {
    return new Parameter()
        .name("X-ENV-ID")
        .in("header")
        .description("Environment ID for authentication.")
        .required(false)
        .schema(new StringSchema());
  }
}
