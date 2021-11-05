package Other.Action;

import com.google.gson.JsonElement;

/**
 * An abstract class representing an Action that a Player can take.
 */
public abstract class Action {

    /**
     * Returns the value that will be returned as a result of the Action
     * @param <V> right now, either String or Connection
     * @return the value of the action
     */
    public abstract <V> V getValue();

    /**
     * Returns the type of Action this action is
     * @return the enum corresponding to the type of action this is
     */
    public abstract ActionEnum getActionType();

    /**
     * Converts the action's result into a Json representation
     * @return the JsonElement representation of the Action's value
     */
    public abstract JsonElement actionToJson();
}
