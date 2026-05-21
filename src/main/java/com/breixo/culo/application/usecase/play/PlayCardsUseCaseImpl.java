package com.breixo.culo.application.usecase.play;

import com.breixo.culo.domain.command.game.PlayCardsCommand;
import com.breixo.culo.domain.model.play.PlayResult;
import com.breixo.culo.domain.model.room.enums.GamePhase;
import com.breixo.culo.domain.port.input.game.PlayCardsUseCase;
import com.breixo.culo.domain.port.input.play.PlayBuilderService;
import com.breixo.culo.domain.port.input.play.PlayExecutionService;
import com.breixo.culo.domain.port.input.play.PlayPolicyValidationService;
import com.breixo.culo.domain.port.input.session.GameSessionContextService;
import com.breixo.culo.domain.port.output.room.RoomSavePersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** The Class PlayCardsUseCaseImpl. */
@Component
@RequiredArgsConstructor
public class PlayCardsUseCaseImpl implements PlayCardsUseCase {

    /** The room save persistence port. */
    private final RoomSavePersistencePort roomSavePersistencePort;

    /** The game session context service. */
    private final GameSessionContextService gameSessionContextService;

    /** The play policy validation service. */
    private final PlayPolicyValidationService playPolicyValidationService;

    /** The play builder service. */
    private final PlayBuilderService playBuilderService;

    /** The play execution service. */
    private final PlayExecutionService playExecutionService;

    /** {@inheritDoc} */
    @Override
    public PlayResult execute(final PlayCardsCommand playCardsCommand) {

        final var gameSessionContext = this.gameSessionContextService.loadWithPhase(
                playCardsCommand.roomCode(),
                playCardsCommand.clientId(),
                GamePhase.PLAYING);

        this.playPolicyValidationService.validatePlayerCanPlay(gameSessionContext);

        final var cards = this.playBuilderService.toCards(
                playCardsCommand.cards(),
                gameSessionContext.room(),
                gameSessionContext.player().id());

        final var play = this.playBuilderService.buildPlay(cards);

        this.playPolicyValidationService.validateLegalPlay(play, gameSessionContext.room());

        final var playExecutionResult = this.playExecutionService.execute(
                gameSessionContext.room(),
                gameSessionContext.player(),
                play);

        final var savedRoom = this.roomSavePersistencePort.save(playExecutionResult.room());

        return PlayResult.builder()
                .room(savedRoom)
                .playerId(gameSessionContext.player().id())
                .play(play)
                .plin(playExecutionResult.plin())
                .roundEnded(playExecutionResult.roundEnded())
                .gameEnded(playExecutionResult.gameEnded())
                .build();
    }
}
