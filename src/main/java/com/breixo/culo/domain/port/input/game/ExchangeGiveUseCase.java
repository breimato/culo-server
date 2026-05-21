package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.command.game.ExchangeGiveCommand;
import com.breixo.culo.domain.model.room.Room;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/** The Interface Exchange Give Use Case. */
public interface ExchangeGiveUseCase {

  /**
   * Execute.
   *
   * @param exchangeGiveCommand the exchange give command.
   * @return the room.
   */
  Room execute(@Valid @NotNull ExchangeGiveCommand exchangeGiveCommand);
}
