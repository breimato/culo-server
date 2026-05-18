package com.breixo.culo.infrastructure.adapter.input.ws.mapper;

import com.breixo.culo.domain.model.Player;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PlayerDto;
import com.breixo.culo.infrastructure.mapper.PlayerRoleMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = PlayerRoleMapper.class)
public interface PlayerDtoMapper {

  @Mapping(target = "connected", source = "connected")
  PlayerDto toPlayerDto(Player player);

  List<PlayerDto> toPlayerDtoList(List<Player> players);
}
