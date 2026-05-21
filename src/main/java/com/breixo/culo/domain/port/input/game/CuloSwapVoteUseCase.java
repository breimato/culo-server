package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.command.game.CuloSwapVoteCommand;
import com.breixo.culo.domain.model.culoswap.CuloSwapVoteResult;

/** The Interface CuloSwapVoteUseCase. */
public interface CuloSwapVoteUseCase {

    /**
     * Execute.
     *
     * @param culoSwapVoteCommand the culo swap vote command
     * @return the culo swap vote result
     */
    CuloSwapVoteResult execute(CuloSwapVoteCommand culoSwapVoteCommand);
}
