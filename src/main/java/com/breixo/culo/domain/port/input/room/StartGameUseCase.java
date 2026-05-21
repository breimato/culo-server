package com.breixo.culo.domain.port.input.room;

import com.breixo.culo.domain.command.room.StartGameCommand;
import com.breixo.culo.domain.model.room.Room;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/** The Interface Start Game Use Case. */
public interface StartGameUseCase {

  /**
   * Execute.
   *
   * @param startGameCommand the start game command.
   * @return the room.
   */
  Room execute(@Valid @NotNull StartGameCommand startGameCommand);
}
