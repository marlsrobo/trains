package Other.Strategies;

import java.util.HashSet;
import java.util.Set;

import Common.PlayerGameState;
import Other.Destination;

/**
 * A new strategy for testing that cheats by picking none of the destinations as their destinations.
 * Otherwise, acts the same as the Buy Now strategy.
 */
public class MockDestinationCheatingStrategy extends AbstractStrategy{
  /**
   * Picks none of the strategies that the referee offered
   *
   * @param availableDestinations the available destinations to pick from
   * @return the two destinations that the strategy has decided to pick
   */
  @Override
  public Set<Destination> pickDestinations(Set<Destination> availableDestinations) {
    return new HashSet<>();
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
    return new BuyNowStrategy().requestToAcquireAConnection(playerGameState);
  }
}
