package harnesses;

import static harnesses.XManager.tournamentResultToJson;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;
import game_state.RailCard;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.List;
import remote.Server;
import remote.Server.ServerBuilder;
import tournament_manager.TournamentResult;
import utils.json.FromJsonConverter;

public class XServer {

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        RunTest(new InputStreamReader(System.in), System.out, port);
    }

    private static void RunTest(Reader input, PrintStream output, int port) {
        JsonStreamParser parser = new JsonStreamParser(input);
        try (input) {
            // Parse JSON
            JsonElement _mapJson = parser.next();
            JsonElement _playersJson = parser.next();
            JsonElement cardsJson = parser.next();

            // Construct objects from JSON
            List<RailCard> cards = FromJsonConverter.cardsFromJson(cardsJson.getAsJsonArray());

            Server trainsServer = new ServerBuilder(port)
                .deckProvider(() -> cards)
                .destinationProvider(XRef::lexicographicOrderOfDestinations)
                .build();

            try {
                TournamentResult tournamentResult = trainsServer.run();
                output.println(tournamentResultToJson(tournamentResult));
            } catch (IllegalArgumentException e) {
                output.println(new JsonPrimitive(e.getMessage()));
            }
        } catch (JsonIOException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
