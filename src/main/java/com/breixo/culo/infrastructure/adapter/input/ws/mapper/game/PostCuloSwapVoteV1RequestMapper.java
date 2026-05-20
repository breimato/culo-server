package com.breixo.culo.infrastructure.adapter.input.ws.mapper.game;

import com.breixo.culo.domain.command.game.CuloSwapVoteCommand;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostCuloSwapVoteV1RequestDto;
import org.mapstruct.Mapper;

/**
 * The Interface PostCuloSwapVoteV1RequestMapper.
 */
@Mapper(componentModel = "spring")
public interface PostCuloSwapVoteV1RequestMapper {

  /**
	 * To culo swap vote command.
	 *
	 * @param postCuloSwapVoteV1RequestDto the post culo swap vote V 1 request dto
	 * @return the culo swap vote command
	 */
  CuloSwapVoteCommand toCuloSwapVoteCommand(PostCuloSwapVoteV1RequestDto postCuloSwapVoteV1RequestDto);
}
