package com.breixo.culo.infrastructure.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The Class CuloProperties.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties(prefix = "culo")
public class CuloProperties {

  /** The cors. */
  private Cors cors = new Cors();

  /** The room. */
  private Room room = new Room();

  /**
	 * The Class Cors.
	 */
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Cors {

    /** The allowed origins. */
    @Builder.Default
    private List<String> allowedOrigins = List.of("http://localhost:5173");
  }

  /**
	 * The Class Room.
	 */
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Room {

    /** The ttl hours. */
    @Builder.Default
    private int ttlHours = 2;

    /** The ack timeout ms. */
    @Builder.Default
    private long ackTimeoutMs = 8000;
  }
}
