package harnesses;

import static harnesses.XManager.playerArrayToMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonStreamParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import map.ITrainMap;
import org.apache.commons.math3.util.Pair;
import player.IPlayer;
import remote.Client;
import utils.json.FromJsonConverter;

public class XClients {

    private static final String DEFAULT_HOST = "127.0.0.1";

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        String host = DEFAULT_HOST;
        if (args.length == 2) {
            host = args[1];
        }
        RunTest(new InputStreamReader(System.in), System.out, host, port);
    }

    private static void RunTest(Reader input, PrintStream output, String host, int port) {
        JsonStreamParser parser = new JsonStreamParser(input);
        try (input) {
            // Parse JSON
            JsonElement mapJson = parser.next();
            JsonElement playersJson = parser.next();
            JsonElement _cardsJson = parser.next();

            // Construct objects from JSON
            ITrainMap map = FromJsonConverter.trainMapFromJson(mapJson);
            List<Pair<String, IPlayer>> players = FromJsonConverter
                .playersFromJson(playersJson.getAsJsonArray(), map);

            for (Pair<String, IPlayer> playerEntry : players) {
                Thread playerThread = new Thread(
                    new Client(host, port, playerEntry.getKey(), playerEntry.getValue()));
                playerThread.start();
            }

        } catch (JsonIOException | IOException ignored) {
        }
    }
}
