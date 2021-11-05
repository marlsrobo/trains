package Other.Player;

import Common.PlayerGameState;
import Common.TrainsMap;
import Other.Action.Action;
import Other.ConnectionColor;
import Other.Destination;

import java.util.Map;
import java.util.Set;

/**
 * An API for the Referee to communicate with a Player in a game of Trains
 */
public interface PlayerAPI {
    /**
     * Sets up this player with the basic game pieces
     * @param map the map of this game of Trains
     * @param rails the number of rails available to this player
     * @param cards the hand of cards available to this player (number per type of colored cards)
     */
    void setup(TrainsMap map, int rails, Map<ConnectionColor, Integer> cards);

    /**
     * Asks this player to pick some destinations for the game relative to the given map
     * @param destinations the set of destinations for the player to choose from
     * @return Set<Destination> the destinations that the Player does not want.
     */
    Set<Destination> pick(Set<Destination> destinations);

    /**
     * Grants this player a turn
     * @param gameState the state of the game for this player
     * @return an Action of either more_cards or acquired (acquired contains a Connection the player wants to acquire)
     */
    Action play(PlayerGameState gameState);

    /**
     * Hands this player some cards
     * @param cards the cards that are given to this player
     */
    void more(Map<ConnectionColor, Integer> cards);

    /**
     * Tells this player if they win.
     * @param winner whether the player has won the game or not.
     */
    void win(boolean winner);
}
