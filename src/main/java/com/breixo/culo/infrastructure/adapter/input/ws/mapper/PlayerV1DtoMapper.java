package com.breixo.culo.infrastructure.adapter.input.ws.mapper;

import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PlayerV1Dto;
import com.breixo.culo.infrastructure.mapper.PlayerRoleMapper;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * The Interface PlayerV1DtoMapper.
 */
@Mapper(componentModel = "spring", uses = PlayerRoleMapper.class)
public interface PlayerV1DtoMapper {

  /**
	 * To player V 1 dto.
	 *
	 * @param player the player
	 * @return the player V 1 dto
	 */
  PlayerV1Dto toPlayerV1Dto(Player player);

  /**
	 * To player V 1 dto list.
	 *
	 * @param players the players
	 * @return the list
	 */
  List<PlayerV1Dto> toPlayerV1DtoList(List<Player> players);
}
