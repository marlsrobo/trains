package utils.json;

import static harnesses.XRef.strategyNameToFilepath;

import action.AcquireConnectionAction;
import action.DrawCardsAction;
import action.TurnAction;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import game_state.IOpponentInfo;
import game_state.IPlayerGameState;
import game_state.OpponentInfo;
import game_state.PlayerGameState;
import game_state.RailCard;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import map.City;
import map.Destination;
import map.ICity;
import map.IRailConnection;
import map.ITrainMap;
import map.MapDimensions;
import map.RailColor;
import map.RailConnection;
import map.TrainMap;
import org.apache.commons.math3.util.Pair;
import player.IPlayer;
import player.Player;
import referee.game_state.PlayerData;
import referee.game_state.TrainsPlayerHand;
import utils.RailCardUtils;
import utils.UnorderedPair;

import java.util.HashSet;
import java.util.Set;

/**
 * Contains static utility methods that are used to convert JsonElements from the gson library into
 * the Java objects that they represent.
 */
public class FromJsonConverter {

    /**
     * Creates a set of pairs of strings that represent destinations in a game of Trains. This
     * method does not validate that the cities exist in any map, or that the pairs are valid
     * Destinations.
     * <p>
     * The Json input will be in the form: [ ["Boston", "New York"], ["Chicago", "Seattle"] ]
     *
     * @param json The Json element containing the array of destinations.
     * @return A set of pairs of strings representing the given array of destinations.
     */
    public static Set<UnorderedPair<String>> fromJsonToUnvalidatedSetOfDestinations(
        JsonElement json) {
        try {
            JsonArray jsonAsArray = json.getAsJsonArray();
            Set<UnorderedPair<String>> destinationsNames = new HashSet<>();
            for (JsonElement jsonDestinationNames : jsonAsArray) {
                destinationsNames.add(fromJsonToUnvalidatedDestination(jsonDestinationNames));
            }
            return destinationsNames;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON");
        }
    }

    /**
     * Creates pair of strings that represent a destination in a game of Trains. This method does
     * not validate that the cities exist in any map, or that the pair is a valid Destinations.
     * <p>
     * The Json input will be in the form: ["Boston", "New York"]
     *
     * @param json The Json element containing the destination.
     * @return A pairs of strings representing the given destination.
     */
    public static UnorderedPair<String> fromJsonToUnvalidatedDestination(JsonElement json) {
        try {
            JsonArray jsonAsArray = json.getAsJsonArray();
            if (jsonAsArray.size() != 2) {
                throw new IllegalArgumentException("Incorrect number of cities within a destination");
            }
            return new UnorderedPair<>(jsonAsArray.get(0).getAsString(),
                jsonAsArray.get(1).getAsString());
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON");
        }
    }

    /**
     * Creates an ITrainMap from JSON input representing a map for the game Trains.
     * <p>
     * The JSON input will be in the form: { "width" : 800, "height": 800, "cities": [["Seattle",
     * [0, 0]], ["Boston", [800, 50]], ["Texas", [500, 800]]], "connections": {"Boston": {"Seattle":
     * {"red": 3}, "Texas": {"green": 5}}, "Seattle": {"Texas": {"blue": 4}}} }
     *
     * @param mapSpecification the JSON element representing the entire map specification.
     * @return An ITrainMap representing the game board specified in the JSON input
     */
    public static ITrainMap trainMapFromJson(JsonElement mapSpecification) {
        try {
            JsonObject mapObject = mapSpecification.getAsJsonObject();
            int width = mapObject.get("width").getAsInt();
            int height = mapObject.get("height").getAsInt();

            Map<String, ICity> cities =
                getCitiesFromJson(width, height, mapObject.getAsJsonArray("cities"));
            Set<ICity> citySet = new HashSet<>(cities.values());

            List<IRailConnection> railConnections =
                getRailConnectionsFromJson(mapObject.getAsJsonObject("connections"), cities);
            Set<IRailConnection> railConnectionSet = new HashSet<>(railConnections);

            return new TrainMap(citySet, railConnectionSet, new MapDimensions(width, height));
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON");
        }
    }

    /**
     * Creates a map of city names to ICity objects for all of the cities specified in the JSON
     * input.
     * <p>
     * The JSON input will be in the form: [["Seattle", [0, 0]], ["Boston", [800, 50]], ["Texas",
     * [500, 800]]]
     *
     * @param mapWidth            The width of the game map in pixels
     * @param mapHeight           The height of the game map in pixels
     * @param citiesSpecification The JSON specifying a list of cities
     * @return A map of city names to ICity objects for all of the cities specified in the JSON
     * input
     */
    public static Map<String, ICity> getCitiesFromJson(int mapWidth, int mapHeight,
        JsonArray citiesSpecification) {
        try {
            Map<String, ICity> cities = new HashMap<>();
            for (JsonElement citySpecification : citiesSpecification) {
                JsonArray cityArray = citySpecification.getAsJsonArray();
                // Extract city information from nested array
                String name = cityArray.get(0).getAsString();
                JsonArray position = cityArray.get(1).getAsJsonArray();
                int xPosition = position.get(0).getAsInt();
                int yPosition = position.get(1).getAsInt();
                // Calculate relative position while creating city
                cities.put(name, new City(name, ((double) xPosition) / ((double) mapWidth),
                    ((double) yPosition) / ((double) mapHeight)));
            }
            return cities;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON");
        }
    }

    /**
     * Creates a list of IRailConnection objects for all of the connections specified in the JSON
     * input.
     * <p>
     * The JSON input will be in the form: {"Boston": {"Seattle": {"red": 3}, "Texas": {"green":
     * 5}}, "Seattle": {"Texas": {"blue": 4}}}
     *
     * @param connectionsSpecification The JSON specifying the connections
     * @param cities                   A map of city names to ICity objects for all cities in the
     *                                 map
     * @return A list of IRailConnection objects for all of the connections specified in the JSON
     * input
     */
    public static List<IRailConnection> getRailConnectionsFromJson(
        JsonObject connectionsSpecification, Map<String, ICity> cities) {
        try {
            List<IRailConnection> railConnections = new ArrayList<>();
            for (Map.Entry<String, JsonElement> connectionEntry : connectionsSpecification
                .entrySet()) {
                ICity startCity = cities.get(connectionEntry.getKey());
                JsonObject targets = connectionEntry.getValue().getAsJsonObject();
                railConnections.addAll(parseConnectionsFromCity(targets, cities, startCity));
            }

            return railConnections;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON");
        }
    }

    /**
     * Creates a list of IRailConnection objects for all of the connections from one city specified
     * in the JSON input.
     * <p>
     * The JSON input will be in the form: {"Seattle": {"red": 3}, "Texas": {"green": 5}}
     * Representing that the given city is connected to Seattle by a red connection with length 3,
     * and to Texas by a green connection of length 5.
     *
     * @param targets   The JSON specifying connections from the given city to other cities
     * @param cities    A map of city names to ICity objects for all cities in the map
     * @param startCity The city that is the source of all connections specified in targets
     * @return A list of IRailConnection objects for all of the connections specified in the JSON
     * input
     */
    private static List<IRailConnection> parseConnectionsFromCity(
        JsonObject targets, Map<String, ICity> cities, ICity startCity) {

        try {
            List<IRailConnection> railConnections = new ArrayList<>();

            // For every city the startCity is connected to, add all associated connections to
            // accumulator railConnections
            for (Map.Entry<String, JsonElement> target : targets.entrySet()) {
                ICity endCity = cities.get(target.getKey());
                UnorderedPair<ICity> endPoints = new UnorderedPair<>(startCity, endCity);
                JsonObject segment = target.getValue().getAsJsonObject();
                railConnections.addAll(parseConnectionsForPairOfCities(segment, endPoints));
            }

            return railConnections;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON");
        }
    }

    /**
     * Parses the JSON 'Segment' object to calculate the specified direct connections between two
     * cities.
     *
     * @param segment   the JSON specifying the connections between two cities.
     * @param endPoints the two cities being connected.
     * @return a list of IRailConnection specified for the two cities.
     */
    public static List<IRailConnection> parseConnectionsForPairOfCities(JsonObject segment,
                                                                        UnorderedPair<ICity> endPoints) {
        try {
            List<IRailConnection> railConnections = new ArrayList<>();

            // For every connection, parse color and length info, and add IRailConnection to accumulator
            for (Map.Entry<String, JsonElement> connection : segment.entrySet()) {
                String color = connection.getKey();
                int length = connection.getValue().getAsInt();
                RailColor railColor = RailColor.valueOf(color.toUpperCase());
                railConnections.add(new RailConnection(endPoints, length, railColor));
            }
            return railConnections;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON");
        }
    }

    /**
     * Creates a list of rail cards that represent the list of cards contained in the given json
     * array.
     * <p>
     * The Json input will be in the form:
     * [
     *      "red",
     *      "blue",
     *      "red",
     *      "white"
     * ]
     *
     * @param cardsJson The Json element containing the array of cards.
     * @return A list of rail cards that represent the list of cards contained in the given json
     *      array.
     */
    public static List<RailCard> cardsFromJson(JsonElement cardsJson) {
        try {
            JsonArray cardsArray = cardsJson.getAsJsonArray();
            List<RailCard> cards = new ArrayList<>();
            for (JsonElement jsonCard : cardsArray) {
                cards.add(RailCardUtils.railCardFromLowercaseCard(jsonCard.getAsString()));
            }
            return cards;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON");
        }
    }

    /**
     * Creates an IPlayerGameState from json that represents one. This method also uses the
     * ITrainMap for the game that contains this IPlayerGameState because IPlayerGameStates contain
     * the destinations the player has selected, which can only be created from a valid ITrainMap.
     *
     * The Json input will be in the form:
     * {
     *      "this" : {
     *          "destination1" : Destination,
     *          "destination2" : Destination,
     *          "rails"        : Natural,
     *          "cards"        : Card*,
     *          "acquired"     : Player
     *       },
     *      "acquired" : [Player, ..., Player]
     * }
     *
     * Where Player is a JSON array of Acquireds,
     * And Acquired is [Name, Name, Color, Length]
     *
     * @param playerStateJson The Json element containing the player game state.
     * @param map The map for the game of Trains that contains this player game state.
     * @return The player game state parsed from the given Json.
     */
    public static IPlayerGameState playerStateFromJson(JsonElement playerStateJson, ITrainMap map) {
        try {
            JsonObject playerObject = playerStateJson.getAsJsonObject();
            JsonObject thisPlayersData = playerObject.getAsJsonObject("this");
            Map<RailCard, Integer> cardsInHand =
                cardsInHandFromJsonObject(thisPlayersData.getAsJsonObject("cards"));

            Set<Destination> destinations = selectedDestinationsFromPlayerState(thisPlayersData,
                map);

            Set<IRailConnection> occupiedConnections = occupiedConnectionsFromJson(playerObject);
            List<IOpponentInfo> opponentConnections = opponentConnectionsFromJson(
                playerObject.get("acquired"));

            return new PlayerGameState(
                new PlayerData(
                    new TrainsPlayerHand(cardsInHand),
                    thisPlayersData.get("rails").getAsInt(),
                    destinations,
                    occupiedConnections),
                opponentConnections);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON");
        }
    }

    /**
     * Creates the list of information known about opponents in a player's game state from the Json
     * that represents it.
     *
     * The Json input will be in the form:
     * [Player, ..., Player]
     *
     * Where Player is a JSON array of Acquireds,
     * And Acquired is [Name, Name, Color, Length]
     *
     * @param opponentJson The known information about opponents in a player game state as Json.
     * @return The known information about opponents in a player game state as a list.
     */
    public static List<IOpponentInfo> opponentConnectionsFromJson(JsonElement opponentJson) {
        try {
            List<IOpponentInfo> opponentConnections = new ArrayList<>();
            for (JsonElement player : opponentJson.getAsJsonArray()) {
                opponentConnections.add(new OpponentInfo(occupiedConnectionsForPlayer(player)));
            }
            return opponentConnections;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON");
        }
    }

    /**
     * Creates the set of destinations that a player has selected from json representing an entire
     * player state. This method also uses the ITrainMap for the game that contains this
     * IPlayerGameState because IPlayerGameStates contain the destinations the player has selected,
     * which can only be created from a valid ITrainMap.
     *
     * The Json input will be in the form:
     * {
     *          "destination1" : Destination,
     *          "destination2" : Destination,
     *          "rails"        : Natural,
     *          "cards"        : Card*,
     *          "acquired"     : Player
     * }
     *
     * @param playerDataJson The Json element containing the data about one player from the player
     *                       game state.
     * @param map The map for the game of Trains that contains this player game data.
     * @return The set of destinations parsed from the given Json.
     */
    public static Set<Destination> selectedDestinationsFromPlayerState(JsonObject playerDataJson,
        ITrainMap map) {
        try {
            UnorderedPair<String> unvalidatedDestination1 = fromJsonToUnvalidatedDestination(
                playerDataJson.get("destination1"));
            UnorderedPair<String> unvalidatedDestination2 = fromJsonToUnvalidatedDestination(
                playerDataJson.get("destination2"));

            Set<Destination> destinations = new HashSet<>();
            destinations.add(convertDestinationNamesToDestination(unvalidatedDestination1, map));
            destinations.add(convertDestinationNamesToDestination(unvalidatedDestination2, map));

            return destinations;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON");
        }
    }

    /**
     * Converts a set of pairs of city names to a set of Destinations that exist in this TrainsMap.
     *
     * @param destinationsNamePairs The set of pairs of names corresponding to city names.
     * @param map The map for the game of Trains that contains this player game data.
     * @return the Set of Destination
     */
    public static Set<Destination> convertDestinationNamesToDestinations(
        Set<UnorderedPair<String>> destinationsNamePairs, ITrainMap map) {
        Set<Destination> destinations = new HashSet<>();
        for (UnorderedPair<String> destinationNames : destinationsNamePairs) {
            destinations.add(convertDestinationNamesToDestination(destinationNames, map));
        }
        return destinations;
    }

    /**
     * Converts the given pair of city names to a Destination if the names exist within this
     * TrainsMap as a Destination.
     *
     * @param destinationCityNames the pair of names for a destination
     * @return the Destination corresponding to the same pair of names given
     */
    public static Destination convertDestinationNamesToDestination(
        UnorderedPair<String> destinationCityNames, ITrainMap map) {
        Set<UnorderedPair<ICity>> mapDestinations = map.getAllPossibleDestinations();
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
     * Creates a map representing the number of each type of card in a hand from a Json array of
     * cards.
     *
     * The Json input will be in the form:
     * ["red", ..., "white"]
     *
     * @param cardsJson The cards in a hand as a Json array.
     * @return The cards in a hand as a map from the type of card to the number of that card in the
     * hand.
     */
    public static Map<RailCard, Integer> cardsInHandFromJsonArray(JsonArray cardsJson) {
        try {
            Map<RailCard, Integer> cardsInHand = new HashMap<>();
            for (JsonElement cardJson : cardsJson) {
                RailCard card = RailCardUtils.railCardFromLowercaseCard(cardJson.getAsString());
                if (cardsInHand.containsKey(card)) {
                    cardsInHand.put(card, cardsInHand.get(card) + 1);
                } else {
                    cardsInHand.put(card, 1);
                }
            }
            return cardsInHand;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON");
        }
    }

    /**
     * Creates a map representing the number of each type of card in a hand from a Json object of
     * cards.
     *
     * The Json input will be in the form:
     * {"red": ..., "blue": ..., ...}
     *
     * @param cardsJson The cards in a hand as a Json object.
     * @return The cards in a hand as a map from the type of card to the number of that card in the
     * hand.
     */
    public static Map<RailCard, Integer> cardsInHandFromJsonObject(JsonObject cardsJson) {
        try {
            Map<RailCard, Integer> cardsInHand = new HashMap<>();

            for (Map.Entry<String, JsonElement> card : cardsJson.entrySet()) {
                if (card.getValue().getAsInt() < 0) {
                    throw new IllegalArgumentException("Negative cards");
                }
                cardsInHand.put(RailCardUtils.railCardFromLowercaseCard(card.getKey()),
                        card.getValue().getAsInt());
            }
            for (RailCard cardType : RailCard.values()) {
                if (!cardsInHand.containsKey(cardType)) {
                    cardsInHand.put(cardType, 0);
                }
            }
            return cardsInHand;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON");
        }
    }

    /**
     * Creates a set of the connections occupied by the player represented by the given player game
     * state as Json.
     *
     * @param playerStateJson A player game state as Json.
     * @return The destinations occupied by the player represented by the given state.
     */
    public static Set<IRailConnection> occupiedConnectionsFromJson(JsonObject playerStateJson) {
        try {
            return new HashSet<>(
                occupiedConnectionsForPlayer(
                    playerStateJson.getAsJsonObject("this").get("acquired").getAsJsonArray()));
        }
        catch (Exception e) {
                throw new IllegalArgumentException("Invalid JSON");
            }
    }

    /**
     * Creates a set of connections from a Json array representing the connections occupied by one
     * player.
     *
     * The Json input will be an array of Acquireds,
     * Where Acquired is [Name, Name, Color, Length]
     *
     * @param player The connections occupied by a player as Json.
     * @return The connections occupied by a player as a set.
     */
    public static Set<IRailConnection> occupiedConnectionsForPlayer(JsonElement player) {
        try {
            Set<IRailConnection> occupiedConnections = new HashSet<>();
            for (JsonElement connection : player.getAsJsonArray()) {
                occupiedConnections.add(acquiredConnectionFromJson(connection));
            }
            return occupiedConnections;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON");
        }
    }

    /**
     * Creates a connection from the Json that represents it.
     *
     * The Json input will be in the form:
     * [Name, Name, Color, Length]
     *
     * @param connectionJson A connection as Json.
     * @return The connection as an object.
     */
    public static IRailConnection acquiredConnectionFromJson(JsonElement connectionJson) {
        try {
            JsonArray jsonArray = connectionJson.getAsJsonArray();
            ICity city1 = new City(jsonArray.get(0).getAsString(), 0, 0);
            ICity city2 = new City(jsonArray.get(1).getAsString(), 0, 0);
            RailColor color = RailCardUtils
                .railColorFromLowercaseColor(jsonArray.get(2).getAsString());
            int length = jsonArray.get(3).getAsInt();
            return new RailConnection(new UnorderedPair<>(city1, city2), length, color);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON");
        }
    }

    /**
     * Creates a turn action from the Json that represents it.
     *
     * The Json input will be either be:
     * [Name, Name, Color, Length]
     * or
     * "more cards"
     *
     * @param turnActionJson A turn action as Json.
     * @return The turn action as an object.
     */
    public static TurnAction turnActionFromJson(JsonElement turnActionJson) {
        try {
            if (turnActionJson.isJsonArray()) {
                IRailConnection acquired = acquiredConnectionFromJson(turnActionJson);
                return new AcquireConnectionAction(acquired);
            }
            if (turnActionJson.getAsString().equals("more cards")) {
                return new DrawCardsAction();
            } else {
                throw new IllegalArgumentException("Action JSON is malformed");
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON");
        }
    }

    public static List<Pair<String, IPlayer>> playersFromJson(JsonArray jsonPlayers, ITrainMap map) {
        try {
            List<Pair<String, IPlayer>> players = new ArrayList<>();
            for (JsonElement jsonPlayer : jsonPlayers) {
                Pair<String, IPlayer> player = playerFromJson(jsonPlayer, map);
                players.add(player);
            }
            return players;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON");
        }
    }

    public static Pair<String, IPlayer> playerFromJson(JsonElement jsonPlayer, ITrainMap map) {
        try {
            JsonArray playerInstance = jsonPlayer.getAsJsonArray();
            String playerName = playerInstance.get(0).getAsString();
            String playerStrategy = playerInstance.get(1).getAsString();
            String strategyFilepath = strategyNameToFilepath(playerStrategy);

            IPlayer player = new Player(strategyFilepath, map);

            return new Pair<>(playerName, player);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON");
        }
    }
}
