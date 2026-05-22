package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.command.game.PassCommand;
import com.breixo.culo.domain.model.game.PassResult;

/** The Interface PassUseCase. */
public interface PassUseCase {

    /**
     * Execute.
     *
     * @param passCommand the pass command
     * @return the pass result
     */
    PassResult execute(PassCommand passCommand);
}
