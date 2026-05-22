package com.breixo.culo.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;

/**
 * The Class SecureRandomConfig.
 */
@Configuration
public class SecureRandomConfig {

  /**
	 * Secure random.
	 *
	 * @return the secure random
	 */
  @Bean
  SecureRandom secureRandom() {
    return new SecureRandom();
  }
}
