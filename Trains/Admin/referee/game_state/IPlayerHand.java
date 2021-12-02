package referee.game_state;

import java.util.Map;

/**
 * The current hand of cards that a Player has.
 * @param <T>
 */
public interface IPlayerHand<T extends Enum<T>> {

  /**
   * Adds a certain amount of cards to this hand of cards
   * @param cardType the suit/color of the card
   * @param amount the number of cards of the cardType to add
   * @throws IllegalArgumentException
   */
  void addCardsToHand(T cardType, int amount) throws IllegalArgumentException;

  /**
   * Getter for the number of cards in this hand of the given suit/color
   * @param cardType the suit/color to count in this hand
   * @return the number of cards of cardType in this hand
   */
  int getNumCardsOfType(T cardType);

  /**
   * Getter for the total number of cards in this hand
   * @return the total number of cards in this hand
   */
  int getTotalNumCards();

  /**
   * Removes a certain amount of cards of the given suit/color from this hand
   * @param cardType the color/suit of card to remove
   * @param amount the number of cards to remove of the given cardType
   * @throws IllegalArgumentException
   * @throws IllegalStateException
   */
  void removeCardsFromHand(T cardType, int amount) throws IllegalArgumentException, IllegalStateException;

  /**
   * Returns a Map representation of this IPlayerHand as a map of card suit/colors to the number of
   * cards of that suit/color in this hand
   * @return the IPlayerHand as a Map representation
   */
  Map<T, Integer> getHand();
}
