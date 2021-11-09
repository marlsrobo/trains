package strategy;

public class DrawCardsAction implements TurnAction {

  @Override
  public void accept(IActionVisitor visitor) {
    visitor.visitCardsAction(this);
  }
}
