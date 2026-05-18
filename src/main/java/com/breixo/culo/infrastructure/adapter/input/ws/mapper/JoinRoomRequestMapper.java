package com.breixo.culo.infrastructure.adapter.input.ws.mapper;

import com.breixo.culo.domain.command.room.JoinRoomCommand;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.JoinRoomRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JoinRoomRequestMapper {

  JoinRoomCommand toJoinRoomCommand(JoinRoomRequestDto joinRoomRequestDto);
}
