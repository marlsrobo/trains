package strategy;

public interface IActionVisitor {

  // TODO visit and apply methods should return a TurnResult

  void visitCardsAction(DrawCardsAction cardsAction);

  void visitAcquireAction(AcquireConnectionAction acquireAction);

  void apply(TurnAction action);

}