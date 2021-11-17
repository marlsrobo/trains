import static org.junit.jupiter.api.Assertions.assertEquals;

import action.DrawCardsAction;
import game_state.IPlayerGameState;
import game_state.RailCard;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import map.Destination;
import map.ITrainMap;
import player.IPlayer;
import action.TurnAction;
import test_utils.TrainsMapUtils;

public class MockDrawCardsPlayer implements IPlayer {
    private int numCardsDrawn;
    private final List<List<RailCard>> expectedDrawnCards;

    public MockDrawCardsPlayer(List<List<RailCard>> expectedDrawnCards) {
        this.numCardsDrawn = 0;
        this.expectedDrawnCards = expectedDrawnCards;
    }

    @Override
    public void setup(ITrainMap map, int numRails, List<RailCard> cards) {

    }

    @Override
    public Set<Destination> chooseDestinations(Set<Destination> options) {
        return new HashSet<>(new ArrayList<>(options).subList(0, 2));
    }

    @Override
    public TurnAction takeTurn(IPlayerGameState playerGameState) {
        return new DrawCardsAction();
    }

    @Override
    public void receiveCards(List<RailCard> drawnCards) {
        assertEquals(this.expectedDrawnCards.get(this.numCardsDrawn), drawnCards);
        this.numCardsDrawn++;
    }

    @Override
    public void winNotification(boolean thisPlayerWon) {

    }

    @Override
    public ITrainMap startTournament(boolean inTournament) {
        return TrainsMapUtils.createDefaultMap();
    }

    @Override
    public void resultOfTournament(boolean thisPlayerWon) {}
}
