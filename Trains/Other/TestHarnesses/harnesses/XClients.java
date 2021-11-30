package harnesses;

import static harnesses.XManager.playersFromJson;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonStreamParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;
import map.ITrainMap;
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
            LinkedHashMap<String, IPlayer> players = playersFromJson(playersJson.getAsJsonArray(),
                map);

            for (Map.Entry<String, IPlayer> playerEntry : players.entrySet()) {
                Thread playerThread = new Thread(
                    new Client(host, port, playerEntry.getKey(), playerEntry.getValue()));
                playerThread.start();
            }

        } catch (JsonIOException | IOException ignored) {
        }
    }
}
