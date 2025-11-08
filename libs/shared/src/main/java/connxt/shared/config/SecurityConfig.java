package connxt.shared.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import connxt.shared.filter.auth.AccessTokenOncePerRequestFilter;
import connxt.shared.filter.auth.AuthenticationStrategy;
import connxt.shared.filter.web.CorrelationIdWebFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  private final String apiPrefix;
  private final String frontendUrl;
  private final RouteConfig routeConfig;
  private final List<AuthenticationStrategy> authenticationStrategies;

  public SecurityConfig(
      @Value("${api.prefix}") String apiPrefix,
      @Value("${api.frontend-url}") String frontendUrl,
      RouteConfig routeConfig,
      List<AuthenticationStrategy> authenticationStrategies) {
    this.apiPrefix = apiPrefix;
    this.frontendUrl = frontendUrl;
    this.routeConfig = routeConfig;
    this.authenticationStrategies = authenticationStrategies;
  }

  @Bean
  public AccessTokenOncePerRequestFilter accessTokenOncePerRequestFilter() {
    return new AccessTokenOncePerRequestFilter(routeConfig, authenticationStrategies);
  }

  @Bean
  public SecurityFilterChain customSecurityFilterChain(
      HttpSecurity http,
      CorsConfigurationSource corsConfigurationSource,
      AccessTokenOncePerRequestFilter accessTokenOncePerRequestFilter)
      throws Exception {
    http.csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .authorizeHttpRequests(
            authz ->
                authz
                    .requestMatchers(routeConfig.getPublicPaths())
                    .permitAll()
                    .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .addFilterAfter(new CorrelationIdWebFilter(), SecurityContextHolderFilter.class)
        .addFilterAfter(accessTokenOncePerRequestFilter, CorrelationIdWebFilter.class);
    return http.build();
  }

  @Bean
  public FilterRegistrationBean<CorsFilter> corsFilter(
      @Qualifier("corsConfigurationSource") CorsConfigurationSource source) {
    FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
    bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return bean;
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    // CORS configuration for public endpoints (CORS free - allow all origins)
    CorsConfiguration publicConfiguration = new CorsConfiguration();
    publicConfiguration.addAllowedOriginPattern("*"); // Allow all origins for public endpoints
    publicConfiguration.setAllowedMethods(Arrays.asList(getAllowedCorsMethods()));
    publicConfiguration.addAllowedHeader("*");
    publicConfiguration.setAllowCredentials(false); // No credentials needed for public endpoints

    // CORS configuration for authenticated endpoints (restricted to frontend URLs)
    CorsConfiguration secureConfiguration = new CorsConfiguration();
    String configuredFrontend = frontendUrl != null ? frontendUrl : "";
    List<String> allowedOrigins =
        Arrays.stream(configuredFrontend.split(","))
            .map(String::trim)
            .filter(origin -> !origin.isEmpty())
            .collect(Collectors.toList());
    if (allowedOrigins.isEmpty()) {
      secureConfiguration.addAllowedOriginPattern("*");
    } else {
      secureConfiguration.setAllowedOriginPatterns(allowedOrigins);
    }
    secureConfiguration.setAllowedMethods(Arrays.asList(getAllowedCorsMethods()));
    secureConfiguration.addAllowedHeader("*");
    secureConfiguration.setAllowCredentials(true);

    // Apply public CORS to public paths (CORS free)
    String[] publicPaths = routeConfig.getPublicPaths();
    for (String path : publicPaths) {
      if (path != null && !path.isBlank()) {
        source.registerCorsConfiguration(path, publicConfiguration);
      }
    }

    // Apply secure CORS to all other endpoints (require JWT token)
    source.registerCorsConfiguration("/**", secureConfiguration);

    return source;
  }

  private String[] getAllowedCorsMethods() {
    return new String[] {"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"};
  }

  @Bean
  public WebMvcConfigurer webMvcConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void configurePathMatch(@NonNull PathMatchConfigurer configurer) {
        configurer.addPathPrefix(
            apiPrefix,
            c ->
                c.isAnnotationPresent(
                    org.springframework.web.bind.annotation.RequestMapping.class));
      }
    };
  }
}
