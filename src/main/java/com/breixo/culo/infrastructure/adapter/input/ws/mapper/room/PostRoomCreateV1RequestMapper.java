package com.breixo.culo.infrastructure.adapter.input.ws.mapper.room;

import com.breixo.culo.domain.command.room.CreateRoomCommand;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostRoomCreateV1RequestDto;
import org.mapstruct.Mapper;

/**
 * The Interface PostRoomCreateV1RequestMapper.
 */
@Mapper(componentModel = "spring")
public interface PostRoomCreateV1RequestMapper {

  /**
	 * To create room command.
	 *
	 * @param postRoomCreateV1RequestDto the post room create V 1 request dto
	 * @return the creates the room command
	 */
  CreateRoomCommand toCreateRoomCommand(PostRoomCreateV1RequestDto postRoomCreateV1RequestDto);
}
