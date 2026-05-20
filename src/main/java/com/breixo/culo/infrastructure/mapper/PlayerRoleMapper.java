package com.breixo.culo.infrastructure.mapper;

import com.breixo.culo.domain.PlayerRole;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PlayerRoleV1Dto;
import org.mapstruct.Mapper;

/**
 * The Interface PlayerRoleMapper.
 */
@Mapper(componentModel = "spring")
public interface PlayerRoleMapper {

  /**
	 * To player role V 1 dto.
	 *
	 * @param playerRole the player role
	 * @return the player role V 1 dto
	 */
  PlayerRoleV1Dto toPlayerRoleV1Dto(PlayerRole playerRole);

  /**
	 * To player role.
	 *
	 * @param playerRoleV1Dto the player role V 1 dto
	 * @return the player role
	 */
  PlayerRole toPlayerRole(PlayerRoleV1Dto playerRoleV1Dto);
}
