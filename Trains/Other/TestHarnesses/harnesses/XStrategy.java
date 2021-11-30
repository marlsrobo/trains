package harnesses;

import static utils.ComparatorUtils.fromUnordered;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonStreamParser;
import game_state.IPlayerGameState;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import map.ICity;
import map.IRailConnection;
import map.ITrainMap;
import strategy.Hold10;
import action.TurnAction;
import utils.OrderedPair;
import utils.json.FromJsonConverter;
import utils.json.ToJsonConverter;

public class XStrategy {

    public static void main(String[] args) {
        RunTest(new InputStreamReader(System.in), System.out);
    }

    private static void RunTest(Reader input, PrintStream output) {
        JsonStreamParser parser = new JsonStreamParser(input);
        try (input) {
            // Parse JSON
            JsonElement mapJson = parser.next();
            JsonElement playerStateJson = parser.next();

            // Construct objects from JSON
            ITrainMap map = FromJsonConverter.trainMapFromJson(mapJson);
            IPlayerGameState playerGameState = FromJsonConverter
                .playerStateFromJson(playerStateJson, map);

            // Calculate and output result
            TurnAction result = new Hold10().takeTurn(playerGameState, map, null);
            output.println(ToJsonConverter.turnActionToJSON(result).toString());
        } catch (JsonIOException | IOException ignored) {
        }
    }
}
