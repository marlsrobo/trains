package referee.game_state;

import game_state.RailCard;
import java.util.Set;
import map.Destination;
import map.IRailConnection;

/**
 * The data that a specific player knows about their own game components.
 */
public interface IPlayerData {
  /**
   * Getter for the cards that the player has in their hand.
   * @return the RailCards that the player has
   */
  IPlayerHand<RailCard> getPlayerHand();

  /**
   * Getter for the number of rails the player currently has.
   * @return the number of rails
   */
  int getNumRails();

  /**
   * Setter for the number of rails for the player
   * @param numRails the new number of rails to set for the player
   * @throws IllegalArgumentException
   * @throws IllegalStateException
   */
  void setNumRails(int numRails) throws IllegalArgumentException, IllegalStateException;

  /**
   * Getter for the destinations that the player has chosen at the beginning of the game.
   * @return the Destinations that the player chose
   */
  Set<Destination> getDestinations();

  /**
   * Getter for the connections that the player has acquired in the game so far.
   * @return the IRailConnections that this player owns/acquires
   */
  Set<IRailConnection> getOwnedConnections();

  /**
   * Returns a defensive copy of this IPlayerData.
   * @return IPlayerData defensive copy
   */
  IPlayerData copyData();
}
