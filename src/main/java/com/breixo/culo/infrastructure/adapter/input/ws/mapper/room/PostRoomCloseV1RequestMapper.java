package com.breixo.culo.infrastructure.adapter.input.ws.mapper.room;

import com.breixo.culo.domain.command.room.CloseRoomCommand;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostRoomCloseV1RequestDto;
import org.mapstruct.Mapper;

/**
 * The Interface PostRoomCloseV1RequestMapper.
 */
@Mapper(componentModel = "spring")
public interface PostRoomCloseV1RequestMapper {

  /**
	 * To close room command.
	 *
	 * @param postRoomCloseV1RequestDto the post room close V 1 request dto
	 * @return the close room command
	 */
  CloseRoomCommand toCloseRoomCommand(PostRoomCloseV1RequestDto postRoomCloseV1RequestDto);
}
