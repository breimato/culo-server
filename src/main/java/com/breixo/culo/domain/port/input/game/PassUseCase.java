package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.command.game.PassCommand;
import com.breixo.culo.domain.model.game.PassResult;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/** The Interface Pass Use Case. */
public interface PassUseCase {

  /**
   * Execute.
   *
   * @param passCommand the pass command.
   * @return the pass result.
   */
  PassResult execute(@Valid @NotNull PassCommand passCommand);
}
