package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.command.game.PlayCardsCommand;
import com.breixo.culo.domain.model.game.PlayResult;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/** The Interface Play Cards Use Case. */
public interface PlayCardsUseCase {

  /**
   * Execute.
   *
   * @param playCardsCommand the play cards command.
   * @return the play result.
   */
  PlayResult execute(@Valid @NotNull PlayCardsCommand playCardsCommand);
}
