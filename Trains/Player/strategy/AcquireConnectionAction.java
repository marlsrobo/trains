package strategy;

public class AcquireConnectionAction implements TurnAction {

  @Override
  public void accept(IActionVisitor visitor) {
    visitor.visitAcquireAction(this);
  }
}
