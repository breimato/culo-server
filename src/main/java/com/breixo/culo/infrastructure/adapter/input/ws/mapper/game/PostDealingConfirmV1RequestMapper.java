package com.breixo.culo.infrastructure.adapter.input.ws.mapper.game;

import com.breixo.culo.domain.command.game.DealCardsCommand;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostDealingConfirmV1RequestDto;
import org.mapstruct.Mapper;

/**
 * The Interface PostDealingConfirmV1RequestMapper.
 */
@Mapper(componentModel = "spring")
public interface PostDealingConfirmV1RequestMapper {

  /**
	 * To deal cards command.
	 *
	 * @param postDealingConfirmV1RequestDto the post dealing confirm V 1 request
	 *                                       dto
	 * @return the deal cards command
	 */
  DealCardsCommand toDealCardsCommand(PostDealingConfirmV1RequestDto postDealingConfirmV1RequestDto);
}
