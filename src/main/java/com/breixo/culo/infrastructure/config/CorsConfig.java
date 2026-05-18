package com.breixo.culo.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@RequiredArgsConstructor
public class CorsConfig {

  /** The culo properties. */
  private final CuloProperties culoProperties;

  @Bean
  public CorsFilter corsFilter() {
    final var corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedOrigins(this.culoProperties.getCors().getAllowedOrigins());
    corsConfiguration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    corsConfiguration.setAllowedHeaders(java.util.List.of("*"));
    corsConfiguration.setAllowCredentials(true);
    final var urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
    urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
    return new CorsFilter(urlBasedCorsConfigurationSource);
  }
}
