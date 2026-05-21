package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.command.game.CuloSwapInitiateCommand;
import com.breixo.culo.domain.model.room.Room;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/** The Interface Culo Swap Initiate Use Case. */
public interface CuloSwapInitiateUseCase {

  /**
   * Execute.
   *
   * @param culoSwapInitiateCommand the culo swap initiate command.
   * @return the room.
   */
  Room execute(@Valid @NotNull CuloSwapInitiateCommand culoSwapInitiateCommand);
}
