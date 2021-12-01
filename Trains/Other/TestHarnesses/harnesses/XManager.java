package harnesses;

import com.google.gson.*;
import game_state.RailCard;
import map.ITrainMap;
import org.apache.commons.math3.util.Pair;
import player.IPlayer;
import tournament_manager.ITournamentManager;
import tournament_manager.SingleElimTournamentManager;
import tournament_manager.TournamentResult;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.*;
import utils.json.FromJsonConverter;
import utils.json.ToJsonConverter;


public class XManager {

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
            List<Pair<String, IPlayer>> players = FromJsonConverter
                .playersFromJson(playersJson.getAsJsonArray(), map);
            List<RailCard> cards = FromJsonConverter.cardsFromJson(cardsJson.getAsJsonArray());

            ITournamentManager manager = new SingleElimTournamentManager.SingleElimTournamentManagerBuilder()
                .deckProvider(() -> cards)
                .destinationProvider(XRef::lexicographicOrderOfDestinations)
                .build();

            try {
                TournamentResult tournamentResult = manager
                    .runTournament(playerArrayToMap(players));
                output.println(ToJsonConverter.tournamentResultToJson(tournamentResult));
            } catch (IllegalArgumentException e) {
                output.println(new JsonPrimitive(e.getMessage()));
            }

        } catch (JsonIOException | IOException ignored) {
        }
    }

    public static LinkedHashMap<String, IPlayer> playerArrayToMap(
        List<Pair<String, IPlayer>> playerList) {
        LinkedHashMap<String, IPlayer> playerMap = new LinkedHashMap<>();
        for (Pair<String, IPlayer> player : playerList) {
            playerMap.put(player.getFirst(), player.getSecond());
        }

        return playerMap;
    }
}
