package com.breixo.culo.infrastructure.mapper;

import com.breixo.culo.domain.PlayerRole;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PlayerRoleDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlayerRoleMapper {

  PlayerRoleDto toPlayerRoleDto(PlayerRole playerRole);

  PlayerRole toPlayerRole(PlayerRoleDto playerRoleDto);
}
