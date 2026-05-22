package com.breixo.culo.infrastructure.adapter.input.ws.mapper.room;

import com.breixo.culo.domain.command.room.LeaveRoomCommand;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostRoomLeaveV1RequestDto;
import org.mapstruct.Mapper;

/** Maps leave room WS request to domain command. */
@Mapper(componentModel = "spring")
public interface PostRoomLeaveV1RequestMapper {

  /**
	 * To leave room command.
	 *
	 * @param postRoomLeaveV1RequestDto the post room leave V 1 request dto
	 * @return the leave room command
	 */
  LeaveRoomCommand toLeaveRoomCommand(PostRoomLeaveV1RequestDto postRoomLeaveV1RequestDto);
}
