package Other.Action;

import com.google.gson.JsonElement;

import Other.Connection;
import Other.JsonTesting.TrainsToJson;

/**
 * Representing the action a player can take to acquire a Connection.
 */
public class AcquiredAction extends Action {
    ActionEnum action = ActionEnum.acquired;
    Connection value;

    /**
     * Constructor for an AcquiredAction
     * @param value the Connection that the Action indicates to the Referee that the Player
     *              wants to acquire
     */
    public AcquiredAction(Connection value) {
        this.value = value;
    }

    /**
     * Returns the value of this Action
     * @return the Connection of this Action
     */
    @Override
    public Connection getValue() {
        return value;
    }

    /**
     * Returns the enum representing what type of action this is
     * @return ActionEnum.acquired
     */
    @Override
    public ActionEnum getActionType() {
        return action;
    }

    /**
     * Converts the value of this Action into a Json representation
     * @return the Json representation of the Connection that this Action's value is
     */
    @Override
    public JsonElement actionToJson() {
        return TrainsToJson.connectionToAcquiredJson(value);
    }

}
