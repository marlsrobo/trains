package strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import action.AcquireConnectionAction;
import action.DrawCardsAction;
import action.TurnAction;
import game_state.IPlayerGameState;
import game_state.RailCard;
import map.City;
import map.Destination;
import map.IRailConnection;
import map.ITrainMap;
import map.RailColor;
import map.RailConnection;
import utils.UnorderedPair;

public class Cheat extends BuyNow {

  /**
   * Returns an action to draw cards if the strategy prefers it or if it is the only option.
   * Returns an action to acquire the most preferred affordable/available connection otherwise.
   *
   * @param currentPlayerGameState the state of the game on which the turn is taken.
   * @param map                    the game map for this game of Trains.
   * @param chosenDestinations
   * @return a action.TurnAction representing this strategy's choice of action.
   */
  @Override
  public TurnAction takeTurn(IPlayerGameState currentPlayerGameState, ITrainMap map,
                             Set<Destination> chosenDestinations) {

    Set<IRailConnection> canAcquire = map.getRailConnections();

    if (canAcquire.isEmpty()) {
      IRailConnection connection = new RailConnection(
              new UnorderedPair<>(
                      new City("Boston", 0, 0),
                      new City("NYC", 1, 1)),
              3, RailColor.BLUE);
      return new AcquireConnectionAction(connection);
    }
    IRailConnection firstConnection = new ArrayList<>(canAcquire).get(0);
    IRailConnection nonExistantConnection = new RailConnection(firstConnection.getCities(),
            ((firstConnection.getLength() + 1) % 3) + 3, firstConnection.getColor());
    return new AcquireConnectionAction(nonExistantConnection);
  }
}
