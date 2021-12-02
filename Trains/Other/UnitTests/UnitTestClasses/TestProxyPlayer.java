import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static test_utils.TrainsMapUtils.createDefaultMap;
import static test_utils.TrainsMapUtils.defaultDestinationProvider;

import action.AcquireConnectionAction;
import action.DrawCardsAction;
import action.TurnAction;
import game_state.IPlayerGameState;
import game_state.OpponentInfo;
import game_state.PlayerGameState;
import game_state.RailCard;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import map.City;
import map.Destination;
import map.IRailConnection;
import map.ITrainMap;
import map.RailColor;
import map.RailConnection;
import org.junit.jupiter.api.Test;
import referee.game_state.PlayerData;
import referee.game_state.TrainsPlayerHand;
import remote.ProxyPlayer;
import utils.UnorderedPair;
import utils.json.ToJsonConverter;

public class TestProxyPlayer {

    @Test
    public void testStart() throws TimeoutException {
        ITrainMap defaultMap = createDefaultMap();
        String input = ToJsonConverter.mapToJson(defaultMap).toString();
        String expectedOutput = "[\"start\",[true]]";

        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        OutputStream outputStream = new ByteArrayOutputStream();
        ProxyPlayer player = new ProxyPlayer(inputStream, outputStream);
        ITrainMap returnedMap = player.startTournament(true);

        assertEquals(expectedOutput, outputStream.toString());
        assertTrue(sameMap(defaultMap, returnedMap));
    }

    @Test
    public void testSetup() throws TimeoutException {
        ITrainMap defaultMap = createDefaultMap();
        List<RailCard> cards = new ArrayList<>(
            Arrays.asList(RailCard.BLUE, RailCard.GREEN, RailCard.RED));

        String input = "void";
        String defaultMapString = ToJsonConverter.mapToJson(defaultMap).toString();
        String cardsString = ToJsonConverter.railCardsToJsonArray(cards).toString();
        String expectedOutput = String
            .format("[\"setup\",[%s,45,%s]]", defaultMapString, cardsString);

        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        OutputStream outputStream = new ByteArrayOutputStream();
        ProxyPlayer player = new ProxyPlayer(inputStream, outputStream);
        player.setup(defaultMap, 45, cards);

        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void testChooseDestinations() throws TimeoutException {
        ITrainMap defaultMap = createDefaultMap();
        List<Destination> allDestinations = defaultDestinationProvider(defaultMap);
        Set<Destination> destinationOptions = new HashSet<>(allDestinations.subList(0, 5));
        Set<Destination> returnedDestinations = new HashSet<>(allDestinations.subList(0, 3));

        String input = ToJsonConverter.destinationsToJson(returnedDestinations).toString();
        String destinationOptionsString = ToJsonConverter.destinationsToJson(destinationOptions)
            .toString();
        String expectedOutput = String.format("[\"pick\",[%s]]", destinationOptionsString);

        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        OutputStream outputStream = new ByteArrayOutputStream();
        ProxyPlayer player = new ProxyPlayer(inputStream, outputStream, defaultMap);
        Set<Destination> actualReturnedDestinations = player.chooseDestinations(destinationOptions);

        assertEquals(expectedOutput, outputStream.toString());
        assertEquals(returnedDestinations, actualReturnedDestinations);
    }

    @Test
    public void testTakeTurnDraw() throws TimeoutException {
        ITrainMap defaultMap = createDefaultMap();
        List<Destination> allDestinations = defaultDestinationProvider(defaultMap);
        Set<Destination> destinations = new HashSet<>(allDestinations.subList(0, 2));
        List<RailCard> cards = new ArrayList<>(
            Arrays.asList(RailCard.BLUE, RailCard.GREEN, RailCard.RED));
        IPlayerGameState gameState = new PlayerGameState(
            new PlayerData(new TrainsPlayerHand(cards), 45, destinations, new HashSet<>()),
            Arrays.asList(new OpponentInfo(new HashSet<>()))
        );

        String input = ToJsonConverter.turnActionToJSON(new DrawCardsAction()).toString();
        String gameStateString = ToJsonConverter.playerGameStateToJson(gameState)
            .toString();
        String expectedOutput = String.format("[\"play\",[%s]]", gameStateString);

        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        OutputStream outputStream = new ByteArrayOutputStream();
        ProxyPlayer player = new ProxyPlayer(inputStream, outputStream, defaultMap);
        TurnAction actualAction = player.takeTurn(gameState);

        assertEquals(expectedOutput, outputStream.toString());
        assertTrue(actualAction instanceof DrawCardsAction);
    }

    @Test
    public void testTakeTurnAcquire() throws TimeoutException {
        ITrainMap defaultMap = createDefaultMap();
        List<Destination> allDestinations = defaultDestinationProvider(defaultMap);
        Set<Destination> destinations = new HashSet<>(allDestinations.subList(0, 2));
        List<RailCard> cards = new ArrayList<>(
            Arrays.asList(RailCard.BLUE, RailCard.GREEN, RailCard.RED));
        IPlayerGameState gameState = new PlayerGameState(
            new PlayerData(new TrainsPlayerHand(cards), 45, destinations, new HashSet<>()),
            Arrays.asList(new OpponentInfo(new HashSet<>()))
        );
        IRailConnection connectionToAcquire = new RailConnection(
            new UnorderedPair<>(new City("Boston", 0, 0),
                new City("New York", 0.1, 0.1)),
            3,
            RailColor.BLUE);

        String input = ToJsonConverter
            .turnActionToJSON(new AcquireConnectionAction(connectionToAcquire)).toString();
        String gameStateString = ToJsonConverter.playerGameStateToJson(gameState)
            .toString();
        String expectedOutput = String.format("[\"play\",[%s]]", gameStateString);

        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        OutputStream outputStream = new ByteArrayOutputStream();
        ProxyPlayer player = new ProxyPlayer(inputStream, outputStream, defaultMap);
        TurnAction actualAction = player.takeTurn(gameState);

        assertEquals(expectedOutput, outputStream.toString());
        assertTrue(actualAction instanceof AcquireConnectionAction);
        assertEquals(connectionToAcquire,
            ((AcquireConnectionAction) actualAction).getRailConnection());
    }

    @Test
    public void testReceiveCards() throws TimeoutException {
        List<RailCard> cards = new ArrayList<>(
            Arrays.asList(RailCard.BLUE, RailCard.GREEN, RailCard.RED));

        String input = "void";
        String cardsString = ToJsonConverter.railCardsToJsonArray(cards).toString();
        String expectedOutput = String
            .format("[\"more\",[%s]]", cardsString);

        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        OutputStream outputStream = new ByteArrayOutputStream();
        ProxyPlayer player = new ProxyPlayer(inputStream, outputStream);
        player.receiveCards(cards);

        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void testWinNotification() throws TimeoutException {
        String input = "void";
        String expectedOutput = "[\"win\",[true]]";

        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        OutputStream outputStream = new ByteArrayOutputStream();
        ProxyPlayer player = new ProxyPlayer(inputStream, outputStream);
        player.winNotification(true);

        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void testResultOfTournament() throws TimeoutException {
        String input = "void";
        String expectedOutput = "[\"end\",[true]]";

        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        OutputStream outputStream = new ByteArrayOutputStream();
        ProxyPlayer player = new ProxyPlayer(inputStream, outputStream);
        player.resultOfTournament(true);

        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void testMalformedJsonResponse() throws TimeoutException, IOException {
        ITrainMap defaultMap = createDefaultMap();

        List<Destination> allDestinations = defaultDestinationProvider(defaultMap);
        Set<Destination> destinationOptions = new HashSet<>(allDestinations.subList(0, 5));
        Set<Destination> returnedDestinations = new HashSet<>(allDestinations.subList(0, 3));

        // only give the first 50 characters as input
        String input = ToJsonConverter.destinationsToJson(returnedDestinations).toString()
            .substring(0, 50);
        String destinationOptionsString = ToJsonConverter.destinationsToJson(destinationOptions)
            .toString();
        String expectedOutput = String.format("[\"pick\",[%s]]", destinationOptionsString);

        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        OutputStream outputStream = new ByteArrayOutputStream();
        ProxyPlayer player = new ProxyPlayer(inputStream, outputStream, defaultMap);
        Set<Destination> actualReturnedDestinations = player.chooseDestinations(destinationOptions);

        assertEquals(expectedOutput, outputStream.toString());
        assertEquals(returnedDestinations, actualReturnedDestinations);
    }

    private static boolean sameMap(ITrainMap map1, ITrainMap map2) {
        boolean sameCities = map1.getCities().equals(map2.getCities());
        boolean sameConnections = map1.getRailConnections().equals(map2.getRailConnections());

        return sameCities && sameConnections;
    }
}
