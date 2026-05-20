package com.breixo.culo.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The Class CuloProperties.
 */
@Getter
@Setter
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
  public static class Cors {

    /** The allowed origins. */
    private List<String> allowedOrigins = List.of("http://localhost:5173");
  }

  /**
	 * The Class Room.
	 */
  @Getter
  @Setter
  public static class Room {

    /** The ttl hours. */
    private int ttlHours = 2;

    /** The ack timeout ms. */
    private long ackTimeoutMs = 8000;
  }
}
