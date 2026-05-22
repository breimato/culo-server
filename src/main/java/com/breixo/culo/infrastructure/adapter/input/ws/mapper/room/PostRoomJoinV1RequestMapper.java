package com.breixo.culo.infrastructure.adapter.input.ws.mapper.room;

import com.breixo.culo.domain.command.room.JoinRoomCommand;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostRoomJoinV1RequestDto;
import org.mapstruct.Mapper;

/**
 * The Interface PostRoomJoinV1RequestMapper.
 */
@Mapper(componentModel = "spring")
public interface PostRoomJoinV1RequestMapper {

  /**
	 * To join room command.
	 *
	 * @param postRoomJoinV1RequestDto the post room join V 1 request dto
	 * @return the join room command
	 */
  JoinRoomCommand toJoinRoomCommand(PostRoomJoinV1RequestDto postRoomJoinV1RequestDto);
}
