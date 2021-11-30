package harnesses;


import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonStreamParser;
import game_state.IPlayerGameState;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import map.IRailConnection;
import map.ITrainMap;
import referee.ActionChecker;
import utils.json.FromJsonConverter;

public class XLegal {

    public static void main(String[] args) {
        RunTest(new InputStreamReader(System.in), System.out);
    }

    private static void RunTest(Reader input, PrintStream output) {
        JsonStreamParser parser = new JsonStreamParser(input);
        try (input) {
            // Parse JSON
            JsonElement mapJson = parser.next();
            JsonElement playerStateJson = parser.next();
            JsonElement desiredConnectionJson = parser.next();

            // Construct objects from JSON
            ITrainMap map = FromJsonConverter.trainMapFromJson(mapJson);
            IRailConnection desiredConnection = FromJsonConverter
                .acquiredConnectionFromJson(desiredConnectionJson);
            IPlayerGameState playerGameState = FromJsonConverter
                .playerStateFromJson(playerStateJson, map);

            // Calculate and output result
            boolean result = new ActionChecker()
                .canAcquireConnection(playerGameState, map, desiredConnection);
            output.println(result);
        } catch (JsonIOException | IOException ignored) {
        }
    }
}
