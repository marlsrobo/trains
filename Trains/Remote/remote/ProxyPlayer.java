package remote;

import action.TurnAction;
import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;
import game_state.IPlayerGameState;
import game_state.RailCard;
import map.Destination;
import map.ICity;
import map.ITrainMap;
import player.IPlayer;
import utils.Constants;
import utils.UnorderedPair;
import utils.json.FromJsonConverter;
import utils.json.ToJsonConverter;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ProxyPlayer implements IPlayer {

    private final BufferedReader input;
    private final PrintWriter output;

    private ITrainMap map;

    public ProxyPlayer(Socket client) throws IOException {
        this.input = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.output = new PrintWriter(client.getOutputStream(), true);
    }

    // sends message to a Player and returns their response
    private void sendMessageToPlayer(String message) {
        this.output.print(message);
    }

    private JsonElement getMessageFromPlayer() throws TimeoutException {
        long startTime = System.currentTimeMillis();
        JsonStreamParser parser = new JsonStreamParser(this.input);
        while (!parser.hasNext()) {
            if (System.currentTimeMillis() - startTime > Constants.TIMEOUT_BETWEEN_INTERACTIONS_MILLISECONDS) {
                throw new TimeoutException("Client took too longer to respond during a turn");
            }
        }
        return parser.next();
    }

    /**
     * Informs the player of the game map, initial number of rails, and starting hand of cards.
     * It is critical to store the game map since it does not change and will not be received from the referee again.
     * It may be important to store the initial hand and rails for destination selection later on.
     *
     * @param map      the map for the entire game.
     * @param numRails the initial number of rails for this player.
     * @param cards    the starting hand of rail cards.
     */
    @Override
    public void setup(ITrainMap map, int numRails, List<RailCard> cards) {
        this.map = map;
        StringBuilder message = new StringBuilder();
        message.append(ToJsonConverter.mapToJson(map));
        message.append(numRails);
        message.append(ToJsonConverter.railCardsToJson(cards));
        this.sendMessageToPlayer(message.toString());
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
        this.sendMessageToPlayer(ToJsonConverter.destinationsToJson(options).toString());

        // get the destinations back from the player
        JsonElement destinationsFromClient = getMessageFromPlayer();
        Set<UnorderedPair<String>> destinationsNamePairs = FromJsonConverter
                .fromJsonToUnvalidatedSetOfDestinations(destinationsFromClient);
        return convertDestinationNamesToDestinations(destinationsNamePairs);
    }

    /**
     * Converts a set of pairs of city names to a set of Destinations that exist in this TrainsMap.
     * @param destinationsNamePairs the set of pairs of names corresponding to city names
     * @return the Set of Destination
     */
    private Set<Destination> convertDestinationNamesToDestinations(Set<UnorderedPair<String>> destinationsNamePairs) {
        Set<Destination> destinations = new HashSet<>();
        for (UnorderedPair<String> destinationNames : destinationsNamePairs) {
            destinations.add(convertDestinationNamesToDestination(destinationNames));
        }
        return destinations;
    }

    /**
     * Converts the given pair of city names to a Destination if the names exist within this TrainsMap as a Destination.
     * Throws an exception if the names don't exist as a destination in this map
     * @param destinationCityNames the pair of names for a destination
     * @return the Destination corresponding to the same pair of names given
     */
    private Destination convertDestinationNamesToDestination(UnorderedPair<String> destinationCityNames) {
        Set<UnorderedPair<ICity>> mapDestinations = this.map.getAllPossibleDestinations();
        for (UnorderedPair<ICity> mapDestination : mapDestinations) {
            UnorderedPair<String> mapDestinationNames =
                    new UnorderedPair<>(mapDestination.left.getName(), mapDestination.right.getName());
            if (destinationCityNames.equals(mapDestinationNames)) {
                return new Destination(mapDestination);
            }
        }
        throw new IllegalArgumentException("Destination doesn't exist in the map");
    }

    /**
     * Returns the action this player would like to take when this method is called to signal it is this player's turn.
     *
     * @param playerGameState the state of the game from this player's perspective at the time the turn action is requested.
     * @return a TurnAction representing this player's desired action for this turn.
     */
    @Override
    public TurnAction takeTurn(IPlayerGameState playerGameState) {
        return null;
    }

    /**
     * Informs the player of the cards drawn as a result of drawing cards on takeTurn(). The cards do
     * not need to be stored since they will appear in the next game state for the next call on takeTurn().
     *
     * @param drawnCards the list of cards drawn this turn.
     */
    @Override
    public void receiveCards(List<RailCard> drawnCards) {

    }

    /**
     * Alerts the player as to whether they have won (implying also that the game is over).
     *
     * @param thisPlayerWon true if this player won, false otherwise.
     */
    @Override
    public void winNotification(boolean thisPlayerWon) {

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
    public ITrainMap startTournament(boolean inTournament) {
        return null;
    }

    /**
     * Tells this player if they won the tournament that they participated in
     *
     * @param thisPlayerWon whether they won the tournament (true = won, false = lost)
     */
    @Override
    public void resultOfTournament(boolean thisPlayerWon) {

    }
}
