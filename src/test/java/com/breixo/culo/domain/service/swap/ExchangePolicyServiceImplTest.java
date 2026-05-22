package com.breixo.culo.domain.service.swap;

import com.breixo.culo.domain.exception.GameException;
import com.breixo.culo.domain.exception.constants.GameExceptionConstants;
import com.breixo.culo.domain.model.room.ExchangeState;
import com.breixo.culo.domain.model.room.Player;
import com.breixo.culo.domain.model.room.Room;
import com.breixo.culo.domain.model.room.enums.PlayerRole;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** The Class Exchange Policy Service Impl Test. */
@ExtendWith(MockitoExtension.class)
class ExchangePolicyServiceImplTest {

    /** The exchange policy service. */
    @InjectMocks
    ExchangePolicyServiceImpl exchangePolicyService;

    /** Test validate not already done when player already exchanged then throw game exception. */
    @Test
    void testValidateNotAlreadyDone_whenPlayerAlreadyExchanged_thenThrowGameException() {
        // Given
        final var player = Instancio.create(Player.class);
        final Set<String> exchangeDone = new HashSet<>();
        exchangeDone.add(player.id());
        final var exchangeState = Instancio.of(ExchangeState.class)
                .set(field(ExchangeState::exchangeDone), exchangeDone)
                .create();
        final var room = Instancio.of(Room.class)
                .set(field(Room::exchangeState), exchangeState)
                .create();

        // When
        final var gameException = assertThrows(
                GameException.class,
                () -> this.exchangePolicyService.validateNotAlreadyDone(room, player));

        // Then
        assertEquals(GameExceptionConstants.EXCHANGE_ALREADY_DONE, gameException.getMessage());
    }

    /** Test validate role can exchange when role is none then throw game exception. */
    @Test
    void testValidateRoleCanExchange_whenRoleIsNone_thenThrowGameException() {
        // Given
        final var player = Instancio.of(Player.class)
                .set(field(Player::role), PlayerRole.NONE)
                .create();

        // When
        final var gameException = assertThrows(
                GameException.class,
                () -> this.exchangePolicyService.validateRoleCanExchange(player));

        // Then
        assertEquals(GameExceptionConstants.NOT_IN_EXCHANGE, gameException.getMessage());
    }
}
