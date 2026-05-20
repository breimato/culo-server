package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.command.game.CuloSwapVoteCommand;
import com.breixo.culo.domain.model.game.CuloSwapVoteResult;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/** The Interface Culo Swap Vote Use Case. */
public interface CuloSwapVoteUseCase {

  /**
   * Execute.
   *
   * @param culoSwapVoteCommand the culo swap vote command.
   * @return the culo swap vote result.
   */
  CuloSwapVoteResult execute(@Valid @NotNull CuloSwapVoteCommand culoSwapVoteCommand);
}
