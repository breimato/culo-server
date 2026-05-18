package com.breixo.culo.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  /** The culo properties. */
  private final CuloProperties culoProperties;

  @Override
  public void configureMessageBroker(final MessageBrokerRegistry messageBrokerRegistry) {
    messageBrokerRegistry.enableSimpleBroker("/topic", "/queue");
    messageBrokerRegistry.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(final StompEndpointRegistry stompEndpointRegistry) {
    stompEndpointRegistry
        .addEndpoint("/ws")
        .setAllowedOriginPatterns(this.culoProperties.getCors().getAllowedOrigins().toArray(String[]::new))
        .withSockJS();
  }
}
