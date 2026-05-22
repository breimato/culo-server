package com.breixo.culo.infrastructure.adapter.output.repository.room;

import lombok.experimental.UtilityClass;

/** Redis key names for room persistence. */
@UtilityClass
public class RoomRedisKeys {

  /** The Constant ROOM_KEY_PREFIX. */
  public static final String ROOM_KEY_PREFIX = "culo:room:";

  /** The Constant ROOM_CODES_INDEX. */
  public static final String ROOM_CODES_INDEX = "culo:room:codes";

  /**
   * Room key for code.
   *
   * @param roomCode the room code
   * @return the redis key
   */
  public static String roomKey(final String roomCode) {
    return ROOM_KEY_PREFIX + roomCode;
  }
}
