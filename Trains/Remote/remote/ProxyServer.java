package remote;

import action.TurnAction;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;
import game_state.IPlayerGameState;
import game_state.RailCard;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import map.Destination;
import map.ITrainMap;
import player.IPlayer;
import utils.UnorderedPair;
import utils.json.FromJsonConverter;
import utils.json.ToJsonConverter;

public class ProxyServer {

    private static final int DISCONNECT_TIMEOUT_MILLIS = 45000000;

    private final Reader input;
    private final PrintWriter output;
    private final IPlayer player;

    private ITrainMap map;

    public ProxyServer(Socket server, IPlayer player) throws IOException {
        this.input = new InputStreamReader(server.getInputStream());
        this.output = new PrintWriter(server.getOutputStream());
        this.player = player;
    }

    public void run() throws TimeoutException, IOException {
        JsonStreamParser parser = new JsonStreamParser(this.input);
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < DISCONNECT_TIMEOUT_MILLIS) {
            // Check both that there are bytes to be read, and that those bytes are valid JSON
            // parser.hasNext() will block forever if given an empty but not closed stream
            if (this.input.ready() && parser.hasNext()) {
                JsonArray methodInfo = parser.next().getAsJsonArray();
                String methodName = methodInfo.get(0).getAsString();

                JsonArray args = methodInfo.get(1).getAsJsonArray();
                JsonElement returnValue;
                switch (methodName) {
                    case "start":
                        returnValue = parseAndRunStart(args);
                        break;
                    case "setup":
                        returnValue = parseAndRunSetup(args);
                        break;
                    case "pick":
                        returnValue = parseAndRunPick(args);
                        break;
                    case "play":
                        returnValue = parseAndRunPlay(args);
                        break;
                    case "more":
                        returnValue = parseAndRunMore(args);
                        break;
                    case "win":
                        returnValue = parseAndRunWin(args);
                        break;
                    case "end":
                        returnValue = parseAndRunEnd(args);
                        break;
                    default:
                        throw new IllegalArgumentException(
                            String.format("Requested method %s does not exist", methodName));
                }
                startTime = System.currentTimeMillis();

                this.output.print(returnValue);
                this.output.flush();
            }
        }
    }

    private JsonElement parseAndRunStart(JsonArray args) throws TimeoutException {
        if (args.size() != 1) {
            throw new IllegalArgumentException("Incorrect number of argument given for start");
        }

        boolean chosen;
        try {
            chosen = args.get(0).getAsBoolean();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Incorrect arguments given for start");
        }

        ITrainMap map = this.player.startTournament(chosen);
        return ToJsonConverter.mapToJson(map);
    }

    private JsonElement parseAndRunSetup(JsonArray args) throws TimeoutException {
        if (args.size() != 3) {
            throw new IllegalArgumentException("Incorrect number of arguments given for setup");
        }

        ITrainMap map;
        int numRails;
        List<RailCard> startingCards;
        try {
            map = FromJsonConverter.trainMapFromJson(args.get(0));
            this.map = map;
            numRails = args.get(1).getAsInt();
            startingCards = FromJsonConverter.cardsFromJson(args.get(2));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Incorrect arguments given for setup");
        }

        this.player.setup(map, numRails, startingCards);
        return new JsonPrimitive("void");
    }

    private JsonElement parseAndRunPick(JsonArray args) throws TimeoutException {
        if (args.size() != 1) {
            throw new IllegalArgumentException("Incorrect number of arguments given for pick");
        }

        Set<Destination> destinationOptions;
        try {
            Set<UnorderedPair<String>> unvalidatedDestinations = FromJsonConverter
                .fromJsonToUnvalidatedSetOfDestinations(args.get(0));
            destinationOptions = FromJsonConverter
                .convertDestinationNamesToDestinations(unvalidatedDestinations, this.map);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Incorrect arguments given for pick");
        }

        Set<Destination> returnedDestinations = this.player.chooseDestinations(destinationOptions);
        return ToJsonConverter.destinationsToJson(returnedDestinations);
    }

    private JsonElement parseAndRunPlay(JsonArray args) throws TimeoutException {
        if (args.size() != 1) {
            throw new IllegalArgumentException("Incorrect number of arguments given for play");
        }

        IPlayerGameState gameState;
        try {
            gameState = FromJsonConverter.playerStateFromJson(args.get(0), this.map);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Incorrect arguments given for play");
        }

        TurnAction action = this.player.takeTurn(gameState);
        return ToJsonConverter.turnActionToJSON(action);
    }

    private JsonElement parseAndRunMore(JsonArray args) throws TimeoutException {
        if (args.size() != 1) {
            throw new IllegalArgumentException("Incorrect number of arguments given for more");
        }

        List<RailCard> cardsDrawn;
        try {
            cardsDrawn = FromJsonConverter.cardsFromJson(args.get(0));
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Incorrect arguments given for more");
        }

        this.player.receiveCards(cardsDrawn);
        return new JsonPrimitive("void");
    }

    private JsonElement parseAndRunWin(JsonArray args) throws TimeoutException {
        if (args.size() != 1) {
            throw new IllegalArgumentException("Incorrect number of arguments given for win");
        }

        boolean thisPlayerWon;
        try {
            thisPlayerWon = args.get(0).getAsBoolean();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Incorrect arguments given for win");
        }

        this.player.winNotification(thisPlayerWon);
        return new JsonPrimitive("void");
    }

    private JsonElement parseAndRunEnd(JsonArray args) throws TimeoutException {
        if (args.size() != 1) {
            throw new IllegalArgumentException("Incorrect number of arguments given for end");
        }

        boolean thisPlayerWon;
        try {
            thisPlayerWon = args.get(0).getAsBoolean();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Incorrect arguments given for end");
        }

        this.player.resultOfTournament(thisPlayerWon);
        return new JsonPrimitive("void");
    }
}
