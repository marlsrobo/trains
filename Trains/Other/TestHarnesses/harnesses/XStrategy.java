package harnesses;

import static harnesses.XLegal.playerStateFromJson;
import static harnesses.XMap.trainMapFromJson;
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

public class XStrategy {
    public  static void main(String[] args) {
        RunTest(new InputStreamReader(System.in), System.out);
    }

    private static void RunTest(Reader input, PrintStream output) {
        JsonStreamParser parser = new JsonStreamParser(input);
        try (input) {
            // Parse JSON
            JsonElement mapJson = parser.next();
            JsonElement playerStateJson = parser.next();

            // Construct objects from JSON
            ITrainMap map = trainMapFromJson(mapJson);
            IPlayerGameState playerGameState = playerStateFromJson(playerStateJson);

            // Calculate and output result
            TurnAction result = new Hold10().takeTurn(playerGameState, map, null);
            output.println(turnActionToJSON(result).toString());
        } catch (JsonIOException | IOException ignored) {
        }
    }

    public static JsonElement turnActionToJSON(TurnAction turnAction) {
        return new ActionToJSONVisitor().apply(turnAction);
    }

    public static JsonElement railConnectionToJSON(IRailConnection railConnection) {
        JsonArray acquired = new JsonArray();

        OrderedPair<ICity> orderedCities = fromUnordered(railConnection.getCities());
        acquired.add(orderedCities.first.getName());
        acquired.add(orderedCities.second.getName());

        acquired.add(railConnection.getColor().name().toLowerCase());
        acquired.add(railConnection.getLength());

        return acquired;
    }
}
