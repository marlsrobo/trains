package Other.JsonTesting;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import Common.PlayerGameState;
import Common.TrainsMap;
import Other.Connection;
import Other.Utils;

/**
 * Takes a JSON representation (from STDIN) of a TrainsMap, the state of the specified player,
 * and which connection is being inquired to see if it can be acquired by the given player.
 * Prints true or false to STDOUT appropriately.
 */
public class JsonLegalAcquisition {

  /**
   * Takes a JSON representation (from STDIN) of a TrainsMap, the state of the specified player,
   * and which connection is being inquired to see if it can be acquired by the given player.
   * Prints true or false to STDOUT appropriately.
   * @param args not used
   */
  public static void main(String[] args) {
    // parse JSON string
    JsonStreamParser parsedJson = new JsonStreamParser(Utils.getJson());

    // getting the map and converting to our data representation
    TrainsMap map = JsonToTrains.jsonToMap(parsedJson.next().getAsJsonObject());
    PlayerGameState playerGameState = JsonToTrains.jsonToPlayerGameState(parsedJson.next().getAsJsonObject(), map);
    Connection acquired = JsonToTrains.jsonToAcquired(parsedJson.next().getAsJsonArray(), map);

    JsonPrimitive isLegalAcquisition = new JsonPrimitive(playerGameState.isLegalAcquisition(acquired));
    System.out.println(isLegalAcquisition);
  }
}
