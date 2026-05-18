package com.breixo.culo.infrastructure.adapter.input.ws.mapper;

import com.breixo.culo.domain.model.room.RoomJoinResult;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.JoinedRoomDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JoinedRoomDtoMapper {

  @Mapping(target = "roomCode", source = "roomCode")
  @Mapping(target = "playerId", source = "playerId")
  JoinedRoomDto toJoinedRoomDto(RoomJoinResult roomJoinResult);
}
