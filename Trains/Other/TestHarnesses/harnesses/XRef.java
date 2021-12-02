package harnesses;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import game_state.RailCard;
import map.Destination;
import map.ITrainMap;
import player.IPlayer;
import player.Player;
import referee.GameEndReport;
import referee.IReferee;
import referee.TrainsReferee;
import utils.ComparatorUtils;
import utils.json.FromJsonConverter;
import utils.json.ToJsonConverter;

public class XRef {

    public static void main(String[] args) {
        RunTest(new InputStreamReader(System.in), System.out);
    }

    private static void RunTest(Reader input, PrintStream output) {
        JsonStreamParser parser = new JsonStreamParser(input);
        try (input) {
            // Parse JSON
            JsonElement mapJson = parser.next();
            JsonElement playersJson = parser.next();
            JsonElement cardsJson = parser.next();

            // Construct objects from JSON
            ITrainMap map = FromJsonConverter.trainMapFromJson(mapJson);
            LinkedHashMap<String, IPlayer> players = playersFromJson(playersJson.getAsJsonArray());
            List<RailCard> cards = FromJsonConverter.cardsFromJson(cardsJson);

            IReferee ref = new TrainsReferee.RefereeBuilder(map, players)
                .deckProvider(() -> cards)
                .destinationProvider(XRef::lexicographicOrderOfDestinations)
                .build();

            try {
                ref.playGame();
            } catch (IllegalArgumentException e) {
                output.println(new JsonPrimitive(e.getMessage()));
                return;
            }

            GameEndReport gameEndReport = ref.calculateGameEndReport();
            // Calculate and output result
            output.println(ToJsonConverter.gameReportToJson(gameEndReport));
        } catch (JsonIOException | IOException ignored) {
        }
    }


    public static List<Destination> lexicographicOrderOfDestinations(ITrainMap map) {
        List<Destination> result =
            map.getAllPossibleDestinations().stream()
                .map((pair) -> new Destination(pair))
                .collect(Collectors.toList());
        result.sort(ComparatorUtils::lexicographicCompareUnorderedPair);
        return result;
    }

    public static LinkedHashMap<String, IPlayer> playersFromJson(JsonArray jsonPlayers) {
        LinkedHashMap<String, IPlayer> players = new LinkedHashMap<>();
        for (JsonElement jsonPlayer : jsonPlayers) {
            Map.Entry<String, IPlayer> player = playerFromJson(jsonPlayer);
            players.put(player.getKey(), player.getValue());
        }
        return players;
    }

    public static Map.Entry<String, IPlayer> playerFromJson(JsonElement jsonPlayer) {
        JsonArray playerInstance = jsonPlayer.getAsJsonArray();
        String playerName = playerInstance.get(0).getAsString();
        String playerStrategy = playerInstance.get(1).getAsString();
        String strategyFilepath = strategyNameToFilepath(playerStrategy);

        IPlayer player = new Player(strategyFilepath);

        return new AbstractMap.SimpleEntry<>(playerName, player);
    }

    public static String strategyNameToFilepath(String strategyName) {
        String defaultStrategyLocation = "../out/production/mark-twain/strategy/";
        return defaultStrategyLocation + strategyName.replace("-", "") + ".class";
    }
}
