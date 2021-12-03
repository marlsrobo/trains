package action;

import game_state.OpponentInfo;

public class DrawCardsAction implements TurnAction {

    @Override
    public <T> T accept(IActionVisitor<T> visitor) {
        return visitor.visitCardsAction(this);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        return obj instanceof DrawCardsAction;
    }
}
