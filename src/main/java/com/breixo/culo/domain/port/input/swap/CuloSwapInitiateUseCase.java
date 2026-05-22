package com.breixo.culo.domain.port.input.swap;

import com.breixo.culo.domain.command.swap.CuloSwapInitiateCommand;
import com.breixo.culo.domain.model.room.Room;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * The Interface CuloSwapInitiateUseCase.
 */
public interface CuloSwapInitiateUseCase {

  /**
	 * Execute.
	 *
	 * @param culoSwapInitiateCommand the culo swap initiate command
	 * @return the room
	 */
  Room execute(@Valid @NotNull CuloSwapInitiateCommand culoSwapInitiateCommand);
}
