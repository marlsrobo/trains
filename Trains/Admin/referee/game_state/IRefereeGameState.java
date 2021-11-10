package referee.game_state;

import game_state.IPlayerGameState;
import game_state.RailCard;
import java.util.List;
import map.IRailConnection;
import referee.ActionChecker;

/**
 * Represents a viewable and modifiable game state for the game Trains starting from after the
 * players have selected their destinations (just before the first turn) until the game has ended
 * (after the last turn concludes). It contains both static information, such as the ITrainMap game
 * board and the order of the players in addition to dynamic information, such as the cards in each
 * player's hand and the occupation status of each rail connection.
 */
public interface IRefereeGameState {

    /**
     * Returns a copy of the ActionChecker object that this class uses to determine if an player's
     * action follows the rules of the game.
     *
     * @return The ActionChecker used by this class.
     */
    ActionChecker getActionChecker();

    void advanceTurn();

    /**
     * Also has the effect of advancing the turn.
     */
    void removeActivePlayer();

    /**
     * Gets the state of the game that is visible to the currently active player. This includes all
     * of the active player's private information, and public information about each other player in
     * the game.
     *
     * @return The game state that is visible to the currently active player.
     */
    IPlayerGameState getActivePlayerState();

    /**
     * Represents the active player choosing to draw cards as their action for their turn. This
     * method will add the given number of cards from the top of the deck, add them to the active
     * player's hand.
     *
     * @param numCards The number of cards to draw.
     * @return a copy of the cards given to the player.
     * @throws IllegalArgumentException if numCards is negative.
     */
    List<RailCard> drawCardsForActivePlayer(int numCards) throws IllegalArgumentException;

    /**
     * Represents the active player choosing to acquire a connection as their action for their turn.
     * This method will attempt to acquire the connection for the active player by removing the
     * appropriate number of rails and cards from the players resources.
     * <p>
     * If the active player is not able to acquire the given connection, the game state is not
     * modified.
     *
     * @param desiredConnection The connection that the active player would like to acquire.
     * @return whether the connection was able to be acquired according to the action checker.
     */
    boolean acquireConnectionForActivePlayer(IRailConnection desiredConnection);

    /**
     *
     * @return
     */
    List<Integer> calculatePlayerScores();
}
