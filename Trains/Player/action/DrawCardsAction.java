package action;

public class DrawCardsAction implements TurnAction {

    @Override
    public <T> T accept(IActionVisitor<T> visitor) {
        return visitor.visitCardsAction(this);
    }
}
