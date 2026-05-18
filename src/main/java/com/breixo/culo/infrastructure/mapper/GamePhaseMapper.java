package com.breixo.culo.infrastructure.mapper;

import com.breixo.culo.domain.GamePhase;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.GamePhaseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GamePhaseMapper {

  GamePhaseDto toGamePhaseDto(GamePhase gamePhase);

  GamePhase toGamePhase(GamePhaseDto gamePhaseDto);
}
