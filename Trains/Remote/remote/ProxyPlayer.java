package remote;

import action.TurnAction;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;
import game_state.IPlayerGameState;
import game_state.RailCard;
import map.Destination;
import map.ITrainMap;
import player.IPlayer;
import utils.Constants;
import utils.UnorderedPair;
import utils.json.FromJsonConverter;
import utils.json.ToJsonConverter;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public class ProxyPlayer implements IPlayer {

    private final Reader input;
    private final PrintWriter output;

    private ITrainMap map;

    public ProxyPlayer(Socket client) throws IOException {
        this.input = new InputStreamReader(client.getInputStream());
        this.output = new PrintWriter(client.getOutputStream(), true);
    }

    // sends message to a Player and returns their response
    private void callMethodOnPlayer(JsonArray message) {
        this.output.print(message);
    }

    private JsonElement getMessageFromPlayer() throws TimeoutException {
        long startTime = System.currentTimeMillis();
        JsonStreamParser parser = new JsonStreamParser(this.input);
        while (!parser.hasNext()) {
            if (System.currentTimeMillis() - startTime
                > Constants.TIMEOUT_BETWEEN_INTERACTIONS_MILLISECONDS) {
                throw new TimeoutException("Client took too longer to respond during a turn");
            }
        }
        return parser.next();
    }

    /**
     * Informs the player of the game map, initial number of rails, and starting hand of cards. It
     * is critical to store the game map since it does not change and will not be received from the
     * referee again. It may be important to store the initial hand and rails for destination
     * selection later on.
     *
     * @param map      the map for the entire game.
     * @param numRails the initial number of rails for this player.
     * @param cards    the starting hand of rail cards.
     */
    @Override
    public void setup(ITrainMap map, int numRails, List<RailCard> cards) throws TimeoutException {
        this.map = map;

        JsonArray methodMessage = new JsonArray();
        methodMessage.add("setup");

        JsonArray args = new JsonArray();
        args.add(ToJsonConverter.mapToJson(map));
        args.add(numRails);
        args.add(ToJsonConverter.railCardsToJson(cards));
        methodMessage.add(args);

        this.callMethodOnPlayer(methodMessage);

        // ensure the player correctly returns "void"
        JsonElement responseFromClient = getMessageFromPlayer();
        assertIsVoid(responseFromClient);
    }

    /**
     * Chooses 2 destinations of the given destination options.
     *
     * @param options the possible destinations to choose from.
     * @return the chosen destinations as a set.
     */
    @Override
    public Set<Destination> chooseDestinations(Set<Destination> options) throws TimeoutException {
        // send the destination options to the player
        JsonArray methodMessage = new JsonArray();
        methodMessage.add("pick");

        JsonArray args = new JsonArray();
        args.add(ToJsonConverter.destinationsToJson(options));
        methodMessage.add(args);

        this.callMethodOnPlayer(methodMessage);

        // get the destinations back from the player
        JsonElement destinationsFromClient = getMessageFromPlayer();
        Set<UnorderedPair<String>> destinationsNamePairs = FromJsonConverter
            .fromJsonToUnvalidatedSetOfDestinations(destinationsFromClient);
        return FromJsonConverter
            .convertDestinationNamesToDestinations(destinationsNamePairs, this.map);
    }

    /**
     * Returns the action this player would like to take when this method is called to signal it is
     * this player's turn.
     *
     * @param playerGameState the state of the game from this player's perspective at the time the
     *                        turn action is requested.
     * @return a TurnAction representing this player's desired action for this turn.
     */
    @Override
    public TurnAction takeTurn(IPlayerGameState playerGameState) throws TimeoutException {
        // send the game state to the player
        JsonArray methodMessage = new JsonArray();
        methodMessage.add("play");

        JsonArray args = new JsonArray();
        args.add(ToJsonConverter.playerGameStateToJson(playerGameState));
        methodMessage.add(args);

        this.callMethodOnPlayer(methodMessage);

        // get the turn action back from the player
        JsonElement turnAction = getMessageFromPlayer();
        return FromJsonConverter.turnActionFromJson(turnAction);
    }

    /**
     * Informs the player of the cards drawn as a result of drawing cards on takeTurn(). The cards
     * do not need to be stored since they will appear in the next game state for the next call on
     * takeTurn().
     *
     * @param drawnCards the list of cards drawn this turn.
     */
    @Override
    public void receiveCards(List<RailCard> drawnCards) throws TimeoutException {
        JsonArray methodMessage = new JsonArray();
        methodMessage.add("more");

        JsonArray args = new JsonArray();
        args.add(ToJsonConverter.railCardsToJson(drawnCards));
        methodMessage.add(args);

        this.callMethodOnPlayer(methodMessage);

        // ensure the player correctly returns "void"
        JsonElement responseFromClient = getMessageFromPlayer();
        assertIsVoid(responseFromClient);
    }

    /**
     * Alerts the player as to whether they have won (implying also that the game is over).
     *
     * @param thisPlayerWon true if this player won, false otherwise.
     */
    @Override
    public void winNotification(boolean thisPlayerWon) throws TimeoutException {
        JsonArray methodMessage = new JsonArray();
        methodMessage.add("win");

        JsonArray args = new JsonArray();
        args.add(thisPlayerWon);
        methodMessage.add(args);

        this.callMethodOnPlayer(methodMessage);

        // ensure the player correctly returns "void"
        JsonElement responseFromClient = getMessageFromPlayer();
        assertIsVoid(responseFromClient);
    }

    /**
     * Alerts the player that they have been chosen to participate in a tournament and that it is
     * about to begin. Asks for the player to return an ITrainsMap to be considered to be used for
     * the tournament.
     *
     * @param inTournament whether the player has been chosen for the tournament
     * @return an implementation of a map to possibly be used for the entire tournament
     */
    @Override
    public ITrainMap startTournament(boolean inTournament) throws TimeoutException {
        JsonArray methodMessage = new JsonArray();
        methodMessage.add("start");

        JsonArray args = new JsonArray();
        args.add(inTournament);
        methodMessage.add(args);

        this.callMethodOnPlayer(methodMessage);

        // get the destinations back from the player
        JsonElement mapJson = getMessageFromPlayer();
        return FromJsonConverter.trainMapFromJson(mapJson);
    }

    /**
     * Tells this player if they won the tournament that they participated in
     *
     * @param thisPlayerWon whether they won the tournament (true = won, false = lost)
     */
    @Override
    public void resultOfTournament(boolean thisPlayerWon) throws TimeoutException {
        JsonArray methodMessage = new JsonArray();
        methodMessage.add("end");

        JsonArray args = new JsonArray();
        args.add(thisPlayerWon);
        methodMessage.add(args);

        this.callMethodOnPlayer(methodMessage);

        // ensure the player correctly returns "void"
        JsonElement responseFromClient = getMessageFromPlayer();
        assertIsVoid(responseFromClient);
    }

    // If the given JsonElement is not the string "void", throw an IllegalArgumentException
    private static void assertIsVoid(JsonElement message) {
        if (!(message.isJsonPrimitive() && message.getAsString().equals("void"))) {
            throw new IllegalArgumentException(
                "Player did not respond with \"void\" to method call expecting no response.");
        }
    }
}
