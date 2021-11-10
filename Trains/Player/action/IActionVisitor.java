package action;

import referee.TrainsReferee.TurnResult;

public interface IActionVisitor<T> {

  T visitCardsAction(DrawCardsAction cardsAction);

  T visitAcquireAction(AcquireConnectionAction acquireAction);

  T apply(TurnAction action);

}