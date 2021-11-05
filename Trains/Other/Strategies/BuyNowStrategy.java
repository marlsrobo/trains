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
 * Class representing the Buy-Now Strategy for a game of Trains.
 */
public class BuyNowStrategy extends AbstractStrategy {

  /**
   * Picks the two destination cards that come last in the lexicographic ordering of the given
   * destinations
   * @param availableDestinations the available destinations to pick from
   * @return the two destinations that this strategy has decided to pick
   */
  @Override
  public Set<Destination> pickDestinations(Set<Destination> availableDestinations) {
    List<Destination> destinationList = new ArrayList<>(availableDestinations);
    Collections.sort(destinationList);
    return new HashSet<>(destinationList.subList(destinationList.size() - Utils.DESTINATIONS_PER_PLAYER, destinationList.size()));
  }

  /**
   * If there are any connections that the player could currently acquire, the strategy decides
   * acquire a connection. Otherwise, the strategy decides to pick more colored cards.
   * @param playerGameState the player's current state of the game
   * @return true if the player should try to acquire a connection
   * false if the player should request more colored cards
   */
  @Override
  public boolean requestToAcquireAConnection(PlayerGameState playerGameState) {
    return playerGameState.findAllAquirableConnections().size() > 0;
  }
}
