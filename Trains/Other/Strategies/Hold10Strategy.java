package Other.Strategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Common.PlayerGameState;
import Other.Destination;
import Other.Utils;

/**
 * Class representing the Hold-10 Strategy for a game of Trains.
 */
public class Hold10Strategy extends AbstractStrategy {

  /**
   * Picks the two destination cards that come first in the lexicographic ordering of the given
   * destinations
   * @param availableDestinations the available destinations to pick from
   * @return the two destinations that this strategy has decided to pick
   */
  @Override
  public Set<Destination> pickDestinations(Set<Destination> availableDestinations) {
    List<Destination> destinationList = new ArrayList<>(availableDestinations);
    Collections.sort(destinationList);
    return new HashSet<>(destinationList.subList(0, Utils.DESTINATIONS_PER_PLAYER));
  }

  /**
   * If the player has less than 10 colored cards or there are not connections that this player
   * could legally acquire, the strategy decides to pick more colored cards. Otherwise, the strategy
   * decides to acquire a connection.
   * @param playerGameState the player's current state of the game
   * @return true if the player should try to acquire a connection
   * false if the player should request more colored cards
   */
  @Override
  public boolean requestToAcquireAConnection(PlayerGameState playerGameState) {
    return playerGameState.getTotalNumCards() >= 10 &&
            playerGameState.findAllAquirableConnections().size() > 0;
  }
}
