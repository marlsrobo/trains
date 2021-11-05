package Other.Strategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Common.PlayerGameState;
import Other.Action.AcquiredAction;
import Other.Action.Action;
import Other.Action.MoreCardsAction;
import Other.Connection;
import Player.Strategy;

/**
 * Abstract Strategy class used to reduce duplicate code between different Trains strategies.
 */
public abstract class AbstractStrategy implements Strategy {

  /**
   * Returns the first Connection from the lexicographically sorted list of connections that are
   * legally acquirable for the player (has enough cards and rails and is unacquired)
   * @param playerGameState the player's current state of the game
   * @return the Connection that the Strategy thinks the player should try to acquire
   */
  @Override
  public Connection pickConnection(PlayerGameState playerGameState) {
    List<Connection> acquirableConnections = new ArrayList<>(playerGameState.findAllAquirableConnections());
    Collections.sort(acquirableConnections);
    return acquirableConnections.get(0);
  }

  /**
   * Picks whether the player should try to acquire a connection or request more colored cards
   * @param playerGameState the player's current state of the game
   * @return Action which is one of "more cards" or an acquired (a Connection to attempt to acquire)
   */
  @Override
  public Action pickTurn(PlayerGameState playerGameState) {
    if (this.requestToAcquireAConnection(playerGameState)) {
      return new AcquiredAction(this.pickConnection(playerGameState));
    }
    else {
      return new MoreCardsAction();
    }
  }
}
