package com.breixo.culo.domain.port.input.swap;

import com.breixo.culo.domain.command.swap.ExchangeGiveCommand;
import com.breixo.culo.domain.model.room.Room;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * The Interface ExchangeGiveUseCase.
 */
public interface ExchangeGiveUseCase {

  /**
	 * Execute.
	 *
	 * @param exchangeGiveCommand the exchange give command
	 * @return the room
	 */
  Room execute(@Valid @NotNull ExchangeGiveCommand exchangeGiveCommand);
}
