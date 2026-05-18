package com.breixo.culo.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "culo")
public class CuloProperties {

  private Cors cors = new Cors();

  private Room room = new Room();

  @Getter
  @Setter
  public static class Cors {

    private List<String> allowedOrigins = List.of("http://localhost:5173");
  }

  @Getter
  @Setter
  public static class Room {

    private int ttlHours = 2;
  }
}
