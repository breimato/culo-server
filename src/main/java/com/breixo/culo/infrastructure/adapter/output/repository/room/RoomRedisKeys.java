package com.breixo.culo.infrastructure.adapter.output.repository.room;

import lombok.experimental.UtilityClass;

/**
 * The Class RoomRedisKeys.
 */
@UtilityClass
public class RoomRedisKeys {

  /** The Constant ROOM_KEY_PREFIX. */
  public static final String ROOM_KEY_PREFIX = "culo:room:";

  /** The Constant ROOM_CODES_INDEX. */
  public static final String ROOM_CODES_INDEX = "culo:room:codes";

  /**
	 * Room key.
	 *
	 * @param roomCode the room code
	 * @return the string
	 */
  public static String roomKey(final String roomCode) {
    return ROOM_KEY_PREFIX + roomCode;
  }
}
