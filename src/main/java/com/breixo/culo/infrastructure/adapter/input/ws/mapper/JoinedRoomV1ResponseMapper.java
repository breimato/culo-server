package com.breixo.culo.infrastructure.adapter.input.ws.mapper;

import com.breixo.culo.domain.model.room.RoomJoinResult;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.JoinedRoomV1ResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * The Interface JoinedRoomV1ResponseMapper.
 */
@Mapper(componentModel = "spring")
public interface JoinedRoomV1ResponseMapper {

  /**
	 * To joined room V 1 response dto.
	 *
	 * @param roomJoinResult the room join result
	 * @return the joined room V 1 response dto
	 */
  @Mapping(target = "roomCode", source = "roomCode")
  JoinedRoomV1ResponseDto toJoinedRoomV1ResponseDto(RoomJoinResult roomJoinResult);
}
