package harnesses;

import static harnesses.XMap.trainMapFromJson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import game_state.IPlayerGameState;
import referee.game_state.PlayerData;
import game_state.PlayerGameState;
import game_state.RailCard;
import referee.game_state.TrainsPlayerHand;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import map.City;
import map.ICity;
import map.IRailConnection;
import map.ITrainMap;
import map.RailColor;
import map.RailConnection;
import referee.ActionChecker;
import utils.RailCardUtils;
import utils.UnorderedPair;

public class XLegal {

    private static final String PLAYER_ID = "irrelevant";

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
            ITrainMap map = trainMapFromJson(mapJson);
            IRailConnection desiredConnection = acquiredConnectionFromJson(desiredConnectionJson);
            IPlayerGameState playerGameState = playerStateFromJson(playerStateJson);

            // Calculate and output result
            boolean result = new ActionChecker()
                .canAcquireConnection(playerGameState, map, desiredConnection);
            output.println(result);
        } catch (JsonIOException | IOException ignored) {
        }
    }

    private static IRailConnection acquiredConnectionFromJson(JsonElement connectionJson) {
        JsonArray jsonArray = connectionJson.getAsJsonArray();
        ICity city1 = new City(jsonArray.get(0).getAsString(), 0, 0);
        ICity city2 = new City(jsonArray.get(1).getAsString(), 0, 0);
        RailColor color = RailCardUtils.railColorFromLowercaseColor(jsonArray.get(2).getAsString());
        int length = jsonArray.get(3).getAsInt();
        return new RailConnection(new UnorderedPair<>(city1, city2), length, color);
    }

    public static IPlayerGameState playerStateFromJson(JsonElement playerStateJson) {
        JsonObject playerObject = playerStateJson.getAsJsonObject();
        JsonObject thisPlayersData = playerObject.getAsJsonObject("this");
        Map<RailCard, Integer> cardsInHand =
            cardsInHandFromJson(thisPlayersData.getAsJsonObject("cards"));

        Map<IRailConnection, String> occupiedConnections = occupiedConnectionsFromJson(
            playerObject);
        // TODO: split up occupied connections by player

        return new PlayerGameState(
            new PlayerData(
                PLAYER_ID,
                new TrainsPlayerHand(cardsInHand),
                thisPlayersData.get("rails").getAsInt(),
                new HashSet<>(),
                occupiedConnections.keySet()),
            new ArrayList<>());
    }

    private static Map<RailCard, Integer> cardsInHandFromJson(JsonObject cardsJson) {
        Map<RailCard, Integer> cardsInHand = new HashMap<>();
        for (String cardString : cardsJson.keySet()) {
            cardsInHand.put(
                RailCardUtils.railCardFromLowercaseCard(cardString),
                cardsJson.get(cardString).getAsInt());
        }
        return cardsInHand;
    }

    private static Map<IRailConnection, String> occupiedConnectionsFromJson(
        JsonObject playerStateJson) {
        Map<IRailConnection, String> occupiedConnections = new HashMap<>();
        for (JsonElement player : playerStateJson.get("acquired").getAsJsonArray()) {
            occupiedConnections.putAll(occupiedConnectionForPlayer(player));
        }
        occupiedConnections.putAll(
            occupiedConnectionForPlayer(
                playerStateJson.getAsJsonObject("this").get("acquired").getAsJsonArray()));
        return occupiedConnections;
    }

    private static Map<IRailConnection, String> occupiedConnectionForPlayer(JsonElement player) {
        Map<IRailConnection, String> occupiedConnections = new HashMap<>();
        for (JsonElement connection : player.getAsJsonArray()) {
            occupiedConnections.put(acquiredConnectionFromJson(connection), PLAYER_ID);
        }
        return occupiedConnections;
    }
}
