package strategy;

import java.util.Optional;
import map.IRailConnection;

/**
 * Represents one player's action on their turn in a game of Trains.
 */
public class TurnAction {

    private final Action actionType;
    private final Optional<IRailConnection> railConnection;

    private TurnAction(Action action, IRailConnection railConnection) {
        this.actionType = action;
        this.railConnection = Optional.ofNullable(railConnection);
    }

    /**
     * Creates a strategy.TurnAction that represents drawing cards from the central deck of rail cards.
     *
     * @return A strategy.TurnAction representing acquiring the given connection.
     */
    public static TurnAction createDrawCards() {
        return new TurnAction(Action.DRAW_CARDS, null);
    }

    /**
     * Creates a strategy.TurnAction that represents acquiring a rail connection on the map in a game of
     * Trains. The acquire connection action also requires the player to specify which connection
     * they would like to acquire.
     *
     * @param railConnection The connection that the player would like to acquire.
     * @return A strategy.TurnAction representing acquiring the given connection.
     */
    public static TurnAction createAcquireConnection(IRailConnection railConnection) {
        return new TurnAction(Action.ACQUIRE_CONNECTION, railConnection);
    }
    
    /**
     * Gets what type of action the player wishes to perform on their turn.
     *
     * @return One of the valid action types for a turn in the game Trains.
     */
    public Action getActionType() {
        return this.actionType;
    }

    /**
     * Gets the connection that the player would like to acquire, only if this Turn action
     * represents acquiring a connection. Otherwise return an empty Optional.
     *
     * @return The connection to acquire or empty.
     */
    public Optional<IRailConnection> getRailConnection() {
        return this.railConnection;
    }
}
