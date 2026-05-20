package com.breixo.culo.infrastructure.mapper;

import com.breixo.culo.domain.GamePhase;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.GamePhaseV1Dto;
import org.mapstruct.Mapper;

/**
 * The Interface GamePhaseMapper.
 */
@Mapper(componentModel = "spring")
public interface GamePhaseMapper {

  /**
	 * To game phase V 1 dto.
	 *
	 * @param gamePhase the game phase
	 * @return the game phase V 1 dto
	 */
  GamePhaseV1Dto toGamePhaseV1Dto(GamePhase gamePhase);

  /**
	 * To game phase.
	 *
	 * @param gamePhaseV1Dto the game phase V 1 dto
	 * @return the game phase
	 */
  GamePhase toGamePhase(GamePhaseV1Dto gamePhaseV1Dto);
}
