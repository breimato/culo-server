package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.command.game.DealCardsCommand;
import com.breixo.culo.domain.model.Room;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/** The Interface Deal Cards Use Case. */
public interface DealCardsUseCase {

  /**
   * Execute.
   *
   * @param dealCardsCommand the deal cards command.
   * @return the room.
   */
  Room execute(@Valid @NotNull DealCardsCommand dealCardsCommand);
}
