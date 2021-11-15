import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import game_state.IPlayerGameState;
import game_state.RailCard;
import java.util.List;
import java.util.Set;
import map.Destination;
import map.ITrainMap;
import player.IPlayer;
import player.Player;
import strategy.Hold10;
import action.TurnAction;
import test_utils.MapUtils;

public class MockEndGameCorrectlyPlayer implements IPlayer {
    private Player underlyingPlayer;

    public MockEndGameCorrectlyPlayer() {
        this.underlyingPlayer = new Player(new Hold10());
    }

    @Override
    public void setup(ITrainMap map, int numRails, List<RailCard> cards) {
        this.underlyingPlayer.setup(map, numRails, cards);
    }

    @Override
    public Set<Destination> chooseDestinations(Set<Destination> options) {
        return this.underlyingPlayer.chooseDestinations(options);
    }

    @Override
    public TurnAction takeTurn(IPlayerGameState playerGameState) {
        assertTrue(playerGameState.getNumRails() > 2);
        return this.underlyingPlayer.takeTurn(playerGameState);
    }

    @Override
    public void receiveCards(List<RailCard> drawnCards) {
        this.underlyingPlayer.receiveCards(drawnCards);
    }

    @Override
    public void winNotification(boolean thisPlayerWon) {
        this.underlyingPlayer.winNotification(thisPlayerWon);
    }

    @Override
    public ITrainMap startTournament(boolean inTournament) {
        return MapUtils.createDefaultMap();
    }

    @Override
    public void resultOfTournament(boolean thisPlayerWon) {}
}