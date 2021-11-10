package player;

import game_state.IPlayerGameState;
import game_state.RailCard;
import java.util.List;
import java.util.Set;
import map.Destination;
import map.ITrainMap;
import action.TurnAction;

/**
 * The Player interface by which the Referee component interacts with the Player for playing a game
 * of Trains.
 */
public interface IPlayer {

  /**
   * Informs the player of the game map, initial number of rails, and starting hand of cards.
   * It is critical to store the game map since it does not change and will not be received from the referee again.
   * It may be important to store the initial hand and rails for destination selection later on.
   * @param map the map for the entire game.
   * @param numRails the initial number of rails for this player.
   * @param cards the starting hand of rail cards.
   */
  void setup(ITrainMap map, int numRails, List<RailCard> cards);

  /**
   * Chooses 2 destinations of the given destination options.
   * @param options the possible destinations to choose from.
   * @return the chosen destinations as a set.
   */
  Set<Destination> chooseDestinations(Set<Destination> options);

  /**
   * Returns the action this player would like to take when this method is called to signal it is this player's turn.
   * @param playerGameState the state of the game from this player's perspective at the time the turn action is requested.
   * @return a TurnAction representing this player's desired action for this turn.
   */
  TurnAction takeTurn(IPlayerGameState playerGameState);

  /**
   * Informs the player of the cards drawn as a result of drawing cards on takeTurn(). The cards do
   * not need to be stored since they will appear in the next game state for the next call on takeTurn().
   * @param drawnCards the list of cards drawn this turn.
   */
  void receiveCards(List<RailCard> drawnCards);

  /**
   * Alerts the player as to whether they have won (implying also that the game is over).
   * @param thisPlayerWon true if this player won, false otherwise.
   */
  void winNotification(boolean thisPlayerWon);
}
