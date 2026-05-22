package com.breixo.culo.infrastructure.adapter.input.ws.mapper.swap;

import com.breixo.culo.domain.command.swap.CuloSwapInitiateCommand;
import com.breixo.culo.infrastructure.adapter.input.ws.dto.PostCuloSwapInitiateV1RequestDto;
import org.mapstruct.Mapper;

/**
 * The Interface PostCuloSwapInitiateV1RequestMapper.
 */
@Mapper(componentModel = "spring")
public interface PostCuloSwapInitiateV1RequestMapper {

  /**
	 * To culo swap initiate command.
	 *
	 * @param postCuloSwapInitiateV1RequestDto the post culo swap initiate V 1
	 *                                         request dto
	 * @return the culo swap initiate command
	 */
  CuloSwapInitiateCommand toCuloSwapInitiateCommand(PostCuloSwapInitiateV1RequestDto postCuloSwapInitiateV1RequestDto);
}
