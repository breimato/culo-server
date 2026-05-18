package com.breixo.culo.infrastructure.config;

public final class WsDestinationConstants {

  public static final String ROOM_TOPIC_PREFIX = "/topic/room/";

  public static final String JOINED_ROOM_SUFFIX = "/joinedRoom";

  public static final String ROOM_STATE_SUFFIX = "/roomState";

  public static final String ERROR_SUFFIX = "/error";

  private WsDestinationConstants() {
  }

  public static String roomTopic(final String roomCode) {
    return ROOM_TOPIC_PREFIX + roomCode;
  }

  public static String playerQueue(final String playerId) {
    return "/queue/player/" + playerId;
  }

  public static String clientTopic(final String clientId) {
    return "/topic/client/" + clientId;
  }
}
