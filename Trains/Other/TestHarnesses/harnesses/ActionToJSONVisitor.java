package harnesses;

import static harnesses.XStrategy.railConnectionToJSON;

import action.AcquireConnectionAction;
import action.DrawCardsAction;
import action.IActionVisitor;
import action.TurnAction;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import referee.TrainsReferee.TurnResult;

public class ActionToJSONVisitor implements IActionVisitor<JsonElement> {

    @Override
    public JsonElement visitCardsAction(DrawCardsAction cardsAction) {
        return new JsonPrimitive("more cards");
    }

    @Override
    public JsonElement visitAcquireAction(AcquireConnectionAction acquireAction) {
        return railConnectionToJSON(acquireAction.getRailConnection());
    }

    @Override
    public JsonElement apply(TurnAction action) {
        return action.accept(this);
    }
}
