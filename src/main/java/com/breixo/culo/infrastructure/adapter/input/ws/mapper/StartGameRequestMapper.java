package com.breixo.culo.infrastructure.adapter.input.ws.mapper;

import com.breixo.culo.domain.command.room.StartGameCommand;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.StartGameRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StartGameRequestMapper {

  StartGameCommand toStartGameCommand(StartGameRequestDto startGameRequestDto);
}
