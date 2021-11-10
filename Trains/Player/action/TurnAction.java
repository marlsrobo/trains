package action;

import referee.TrainsReferee.TurnResult;

/**
 * Represents one player's action on their turn in a game of Trains.
 */
public interface TurnAction {

    <T> T accept(IActionVisitor<T> visitor);
}
