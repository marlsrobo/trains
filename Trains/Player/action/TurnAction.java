package action;

/**
 * Represents one player's action on their turn in a game of Trains.
 */
public interface TurnAction {

    <T> T accept(IActionVisitor<T> visitor);
}
