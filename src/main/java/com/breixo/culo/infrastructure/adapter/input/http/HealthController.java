package com.breixo.culo.infrastructure.adapter.input.http;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * The Class HealthController.
 */
@RestController
public class HealthController {

  /**
	 * Health.
	 *
	 * @return the map
	 */
  @GetMapping("/health")
  public Map<String, String> health() {
    return Map.of("status", "ok");
  }
}
