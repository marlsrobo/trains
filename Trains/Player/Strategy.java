package Player;

import java.io.Serializable;
import java.util.Set;

import Common.PlayerGameState;
import Other.Action.Action;
import Other.Connection;
import Other.Destination;

/**
 * A Strategy interface that will make decisions for a player in a game of Trains.
 */
public interface Strategy {

  /**
   * Picks two destinations from the given available destinations
   * @param availableDestinations the available destinations to pick from
   * @return the two destinations that the strategy has decided to pick
   */
  Set<Destination> pickDestinations(Set<Destination> availableDestinations);

  /**
   * Determines whether the player should try to acquire a connection or request more colored cards
   * @param playerGameState the player's current state of the game
   * @return true if the player should try to acquire a connection
   *         false if the player should request more colored cards
   */
  boolean requestToAcquireAConnection(PlayerGameState playerGameState);

  /**
   * Picks whether the player should try to acquire a connection or request more colored cards
   * @param playerGameState the player's current state of the game
   * @return Action which is one of "more cards" or an acquired (a Connection to attempt to acquire)
   */
  Action pickTurn(PlayerGameState playerGameState);

  /**
   * Returns the Connection that the player should try to acquire
   * @param playerGameState the player's current state of the game
   * @return the Connection that the Strategy thinks the player should try to acquire
   */
  Connection pickConnection(PlayerGameState playerGameState);
}
