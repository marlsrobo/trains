package Other.Strategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Common.PlayerGameState;
import Other.Connection;
import Other.ConnectionColor;
import Other.Destination;
import Other.Utils;

/**
 * A new strategy for testing that cheats by picking a connection that has already been acquired,
 * or if there aren't any connections that have been acquired, returns null as the connection to pick.
 */
public class MockAcquiringCheatingStrategy extends AbstractStrategy{

  @Override
  public Connection pickConnection(PlayerGameState playerGameState) {
    Map<Connection, Integer> connectionStatuses = playerGameState.getConnectionsStatus();
    for (Connection connection : connectionStatuses.keySet()) {
      if (connectionStatuses.get(connection) != Utils.NOT_ACQUIRED_CONNECTION_STATUS) {
        return connection;
      }
    }
    return null;
  }

  /**
   * Picks none of the strategies that the referee offered
   *
   * @param availableDestinations the available destinations to pick from
   * @return the two destinations that the strategy has decided to pick
   */
  @Override
  public Set<Destination> pickDestinations(Set<Destination> availableDestinations) {
    return new Hold10Strategy().pickDestinations(availableDestinations);
  }

  /**
   * Determines whether the player should try to acquire a connection or request more colored cards
   *
   * @param playerGameState the player's current state of the game
   * @return true if the player should try to acquire a connection
   * false if the player should request more colored cards
   */
  @Override
  public boolean requestToAcquireAConnection(PlayerGameState playerGameState) {
    return new Hold10Strategy().requestToAcquireAConnection(playerGameState);
  }
}
