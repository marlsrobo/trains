package Other.JsonTesting;

import com.google.gson.JsonStreamParser;

import Common.PlayerGameState;
import Common.TrainsMap;
import Other.Action.Action;
import Other.Strategies.Hold10Strategy;
import Other.Utils;

/**
 * Takes a JSON representation (from STDIN) of a TrainsMap and the state of a specified player.
 * Returns to STDOUT the corresponding result of a Hold-10 Strategy's decision for what kind
 * of turn the player should make.
 */
public class JsonHold10TurnDecision {

  /**
   * Takes a JSON representation (from STDIN) of a TrainsMap and the state of the specified player.
   * Prints "more cards" to STDOUT if the Hold-10 Strategy decides that the player should pick more cards
   * Prints an Acquired ([Name, Name, Color, Length]) to STDOUT representing the Connection that the strategy
   * thinks the Player should choose to acquire if that type of turn is chosen.
   * @param args not used
   */
  public static void main(String[] args) {
    // parse JSON string
    JsonStreamParser parsedJson = new JsonStreamParser(Utils.getJson());

    // getting the map and converting to our data representation
    TrainsMap map = JsonToTrains.jsonToMap(parsedJson.next().getAsJsonObject());
    PlayerGameState playerGameState = JsonToTrains.jsonToPlayerGameState(parsedJson.next().getAsJsonObject(), map);

    // getting the result of the strategy's decision
    Hold10Strategy hold10Strategy = new Hold10Strategy();
    Action turnAction = hold10Strategy.pickTurn(playerGameState);

    // print the result
    System.out.println(turnAction.actionToJson());
  }
}
