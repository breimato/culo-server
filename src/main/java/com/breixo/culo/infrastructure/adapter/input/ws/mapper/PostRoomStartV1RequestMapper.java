package com.breixo.culo.infrastructure.adapter.input.ws.mapper;

import com.breixo.culo.domain.command.room.StartGameCommand;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostRoomStartV1RequestDto;
import org.mapstruct.Mapper;

/**
 * The Interface PostRoomStartV1RequestMapper.
 */
@Mapper(componentModel = "spring")
public interface PostRoomStartV1RequestMapper {

  /**
	 * To start game command.
	 *
	 * @param postRoomStartV1RequestDto the post room start V 1 request dto
	 * @return the start game command
	 */
  StartGameCommand toStartGameCommand(PostRoomStartV1RequestDto postRoomStartV1RequestDto);
}
