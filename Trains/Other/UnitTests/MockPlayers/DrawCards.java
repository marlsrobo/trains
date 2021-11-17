import action.DrawCardsAction;
import action.TurnAction;
import game_state.IPlayerGameState;
import game_state.RailCard;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import map.Destination;
import map.ITrainMap;
import player.IPlayer;
import test_utils.TrainsMapUtils;

public class DrawCards implements IPlayer {

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

    }

    @Override
    public void winNotification(boolean thisPlayerWon) {

    }

    @Override
    public ITrainMap startTournament(boolean inTournament) {
        return TrainsMapUtils.createDefaultMap();
    }

    @Override
    public void resultOfTournament(boolean thisPlayerWon) {

    }
}
