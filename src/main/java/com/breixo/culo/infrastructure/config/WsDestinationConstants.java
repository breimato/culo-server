package com.breixo.culo.infrastructure.config;

/**
 * The Class WsDestinationConstants.
 */
public final class WsDestinationConstants {

  /** The Constant ROOM_TOPIC_PREFIX. */
  public static final String ROOM_TOPIC_PREFIX = "/topic/room/";

  /** The Constant JOINED_ROOM_SUFFIX. */
  public static final String JOINED_ROOM_SUFFIX = "/joinedRoom";

  /** The Constant ROOM_STATE_SUFFIX. */
  public static final String ROOM_STATE_SUFFIX = "/roomState";

  /** The Constant ERROR_SUFFIX. */
  public static final String ERROR_SUFFIX = "/error";

  /**
	 * Instantiates a new ws destination constants.
	 */
  private WsDestinationConstants() {
  }

  /**
	 * Room topic.
	 *
	 * @param roomCode the room code
	 * @return the string
	 */
  public static String roomTopic(final String roomCode) {
    return ROOM_TOPIC_PREFIX + roomCode;
  }

  /**
	 * Player queue.
	 *
	 * @param playerId the player id
	 * @return the string
	 */
  public static String playerQueue(final String playerId) {
    return "/queue/player/" + playerId;
  }

  /**
	 * Client topic.
	 *
	 * @param clientId the client id
	 * @return the string
	 */
  public static String clientTopic(final String clientId) {
    return "/topic/client/" + clientId;
  }
}
