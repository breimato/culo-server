package com.breixo.culo.domain.port.input.game;

import com.breixo.culo.domain.command.game.PlayCardsCommand;
import com.breixo.culo.domain.model.game.PlayResult;

/**
 * The Interface PlayCardsUseCase.
 */
public interface PlayCardsUseCase {

    /**
	 * Execute.
	 *
	 * @param playCardsCommand the play cards command
	 * @return the play result
	 */
    PlayResult execute(PlayCardsCommand playCardsCommand);
}
