package com.breixo.culo.infrastructure.adapter.input.ws.mapper;

import com.breixo.culo.domain.command.room.CreateRoomCommand;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.CreateRoomRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreateRoomRequestMapper {

  CreateRoomCommand toCreateRoomCommand(CreateRoomRequestDto createRoomRequestDto);
}
