package Other.Action;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * Representing the action a player can take to request more cards.
 */
public class MoreCardsAction extends Action {
    ActionEnum action = ActionEnum.more_cards;
    String value = "more cards";

    /**
     * Returns the value that will be returned as a result of the Action
     *
     * @return the value of the action
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * Returns the type of Action this action is
     *
     * @return the enum corresponding to the type of action this is
     */
    @Override
    public ActionEnum getActionType() {
        return action;
    }

    /**
     * Converts the action's result into a Json representation
     *
     * @return the JsonElement representation of the Action's value
     */
    @Override
    public JsonElement actionToJson() {
        return new JsonPrimitive(value);
    }
}