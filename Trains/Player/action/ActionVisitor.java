package action;

import static referee.TrainsReferee.PLAYER_NUM_CARDS_PER_DRAW;

import game_state.RailCard;
import java.util.ArrayList;
import java.util.List;
import player.IPlayer;
import referee.TrainsReferee.TurnResult;
import referee.game_state.IRefereeGameState;

public class ActionVisitor implements IActionVisitor<TurnResult> {

    private final IRefereeGameState gameState;
    private final IPlayer activePlayer;

    public ActionVisitor(IRefereeGameState gameState, IPlayer activePlayer) {
        this.gameState = gameState;
        this.activePlayer = activePlayer;
    }

    @Override
    public TurnResult visitCardsAction(DrawCardsAction cardsAction) {
        List<RailCard> drawnCards = this.gameState
            .drawCardsForActivePlayer(PLAYER_NUM_CARDS_PER_DRAW);
        this.activePlayer.receiveCards(new ArrayList<>(drawnCards));
        return drawnCards.isEmpty() ? TurnResult.INSIGNIFICANT : TurnResult.SIGNIFICANT;
    }

    @Override
    public TurnResult visitAcquireAction(AcquireConnectionAction acquireAction) {
        boolean connectionAcquired =
            this.gameState.acquireConnectionForActivePlayer(acquireAction.getRailConnection());
        return connectionAcquired ? TurnResult.SIGNIFICANT : TurnResult.INVALID;
    }

    @Override
    public TurnResult apply(TurnAction action) {
        return action.accept(this);
    }
}
