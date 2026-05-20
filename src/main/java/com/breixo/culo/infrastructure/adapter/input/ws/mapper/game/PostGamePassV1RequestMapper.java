package com.breixo.culo.infrastructure.adapter.input.ws.mapper.game;

import com.breixo.culo.domain.command.game.PassCommand;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostGamePassV1RequestDto;
import org.mapstruct.Mapper;

/**
 * The Interface PostGamePassV1RequestMapper.
 */
@Mapper(componentModel = "spring")
public interface PostGamePassV1RequestMapper {

  /**
	 * To pass command.
	 *
	 * @param postGamePassV1RequestDto the post game pass V 1 request dto
	 * @return the pass command
	 */
  PassCommand toPassCommand(PostGamePassV1RequestDto postGamePassV1RequestDto);
}
