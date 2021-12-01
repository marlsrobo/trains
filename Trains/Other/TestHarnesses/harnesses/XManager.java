package harnesses;

import com.google.gson.*;
import game_state.RailCard;
import map.ITrainMap;
import org.apache.commons.math3.util.Pair;
import player.IPlayer;
import player.Player;
import tournament_manager.ITournamentManager;
import tournament_manager.SingleElimTournamentManager;
import tournament_manager.TournamentResult;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.*;
import utils.json.FromJsonConverter;

import static harnesses.XRef.*;


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
                TournamentResult tournamentResult = manager.runTournament(players);
                output.println(tournamentResultToJson(tournamentResult));
            } catch (IllegalArgumentException e) {
                output.println(new JsonPrimitive(e.getMessage()));
            }

        } catch (JsonIOException | IOException ignored) {
        }
    }

    private static LinkedHashMap<String, IPlayer> playerArrayToMap(
        List<Pair<String, IPlayer>> playerList) {

    }

    public static JsonArray tournamentResultToJson(TournamentResult tournamentResult) {
        JsonArray reportJson = new JsonArray();
        List<String> winners = new ArrayList<>(tournamentResult.getWinners());
        List<String> cheaters = new ArrayList<>(tournamentResult.getCheaters());
        reportJson.add(rankToJson(winners));
        reportJson.add(rankToJson(cheaters));
        return reportJson;
    }

}
