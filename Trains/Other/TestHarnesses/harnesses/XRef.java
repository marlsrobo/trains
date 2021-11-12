package harnesses;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonStreamParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import action.TurnAction;
import game_state.IPlayerGameState;
import map.ITrainMap;
import player.IPlayer;
import strategy.Hold10;

import static harnesses.XLegal.playerStateFromJson;
import static harnesses.XMap.trainMapFromJson;

public class XRef {

  public  static void main(String[] args) {
    RunTest(new InputStreamReader(System.in), System.out);
  }

  private static void RunTest(Reader input, PrintStream output) {
    JsonStreamParser parser = new JsonStreamParser(input);
    try (input) {
      // Parse JSON
      JsonElement mapJson = parser.next();
      JsonElement players = parser.next();
      JsonElement cards = parser.next();

      // Construct objects from JSON
      ITrainMap map = trainMapFromJson(mapJson);
      IPlayerGameState playerGameState = playerStateFromJson(playerStateJson);

      // Calculate and output result
      TurnAction result = new Hold10().takeTurn(playerGameState, map, null);
      output.println(turnActionToJSON(result).toString());
    } catch (JsonIOException | IOException ignored) {
    }
  }

  public List<IPlayer> playersFromJson(JsonArray jsonPlayers) {
    List<IPlayer> players = new ArrayList<>();
    for (JsonElement jsonPlayer : jsonPlayers) {
      players.add(playerFromJson(jsonPlayer));
    }
    return players;
  }

  private IPlayer playerFromJson(JsonElement jsonPlayer) {


  }

}
