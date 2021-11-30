package utils.json;

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
import referee.game_state.PlayerData;
import referee.game_state.TrainsPlayerHand;
import utils.Constants;
import utils.RailCardUtils;
import utils.UnorderedPair;

import java.util.HashSet;
import java.util.Set;

public class FromJsonConverter {

    public static Set<UnorderedPair<String>> fromJsonToUnvalidatedSetOfDestinations(
        JsonElement json) {
        JsonArray jsonAsArray = json.getAsJsonArray();
        Set<UnorderedPair<String>> destinationsNames = new HashSet<>();
        for (JsonElement jsonDestinationNames : jsonAsArray) {
            destinationsNames.add(fromJsonToUnvalidatedDestination(jsonDestinationNames));
        }
        return destinationsNames;
    }

    public static UnorderedPair<String> fromJsonToUnvalidatedDestination(JsonElement json) {
        JsonArray jsonAsArray = json.getAsJsonArray();
        if (jsonAsArray.size() != 2) {
            throw new IllegalArgumentException("Incorrect number of cities within a destination");
        }
        return new UnorderedPair<>(jsonAsArray.get(0).getAsString(),
            jsonAsArray.get(1).getAsString());
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
    private static Map<String, ICity> getCitiesFromJson(int mapWidth, int mapHeight,
        JsonArray citiesSpecification) {

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
    private static List<IRailConnection> getRailConnectionsFromJson(
        JsonObject connectionsSpecification, Map<String, ICity> cities) {

        List<IRailConnection> railConnections = new ArrayList<>();
        for (Map.Entry<String, JsonElement> connectionEntry : connectionsSpecification.entrySet()) {
            ICity startCity = cities.get(connectionEntry.getKey());
            JsonObject targets = connectionEntry.getValue().getAsJsonObject();
            railConnections.addAll(parseConnectionsFromCity(targets, cities, startCity));
        }

        return railConnections;
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

        List<IRailConnection> railConnections = new ArrayList<>();

        // For every city the startCity is connected to, add all associated connections to
        // accumulator railConnections
        for (Map.Entry<String, JsonElement> target : targets.entrySet()) {
            ICity endCity = cities.get(target.getKey());
            UnorderedPair<ICity> endPoints = new UnorderedPair<>(startCity, endCity);
            JsonObject segment = target.getValue().getAsJsonObject();
            railConnections.addAll(parseConnections(segment, endPoints));
        }

        return railConnections;
    }

    /**
     * Parses the JSON 'Segment' object to calculate the specified direct connections between two
     * cities.
     *
     * @param segment   the JSON specifying the connections between two cities.
     * @param endPoints the two cities being connected.
     * @return a list of IRailConnection specified for the two cities.
     */
    private static List<IRailConnection> parseConnections(JsonObject segment,
        UnorderedPair<ICity> endPoints) {
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

    public static List<RailCard> cardsFromJson(JsonElement cardsJson) {
        JsonArray cardsArray = cardsJson.getAsJsonArray();
        if (cardsArray.size() != Constants.DECK_SIZE) {
            throw new IllegalArgumentException("Deck size is not " + Constants.DECK_SIZE);
        }
        List<RailCard> cards = new ArrayList<>();
        for (JsonElement jsonCard : cardsArray) {
            cards.add(RailCardUtils.railCardFromLowercaseCard(jsonCard.getAsString()));
        }
        return cards;
    }

    public static IPlayerGameState playerStateFromJson(JsonElement playerStateJson, ITrainMap map) {
        JsonObject playerObject = playerStateJson.getAsJsonObject();
        JsonObject thisPlayersData = playerObject.getAsJsonObject("this");
        Map<RailCard, Integer> cardsInHand =
            cardsInHandFromJson(thisPlayersData.getAsJsonObject("cards"));

        Set<Destination> destinations = selectedDestinationsFromPlayerState(thisPlayersData, map);

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

    private static List<IOpponentInfo> opponentConnectionsFromJson(JsonElement opponentAcquired) {
        List<IOpponentInfo> opponentConnections = new ArrayList<>();
        for (JsonElement player : opponentAcquired.getAsJsonArray()) {
            opponentConnections.add(new OpponentInfo(occupiedConnectionForPlayer(player)));
        }
        return opponentConnections;
    }

    private static Set<Destination> selectedDestinationsFromPlayerState(JsonObject playerDataJson,
        ITrainMap map) {
        UnorderedPair<String> unvalidatedDestination1 = fromJsonToUnvalidatedDestination(
            playerDataJson.get("destination1"));
        UnorderedPair<String> unvalidatedDestination2 = fromJsonToUnvalidatedDestination(
            playerDataJson.get("destination2"));

        Set<Destination> destinations = new HashSet<>();
        destinations.add(convertDestinationNamesToDestination(unvalidatedDestination1, map));
        destinations.add(convertDestinationNamesToDestination(unvalidatedDestination2, map));

        return destinations;
    }

    /**
     * Converts a set of pairs of city names to a set of Destinations that exist in this TrainsMap.
     *
     * @param destinationsNamePairs the set of pairs of names corresponding to city names
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
     * TrainsMap as a Destination. Throws an exception if the names don't exist as a destination in
     * this map
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

    private static Map<RailCard, Integer> cardsInHandFromJson(JsonObject cardsJson) {
        Map<RailCard, Integer> cardsInHand = new HashMap<>();
        for (String cardString : cardsJson.keySet()) {
            cardsInHand.put(
                RailCardUtils.railCardFromLowercaseCard(cardString),
                cardsJson.get(cardString).getAsInt());
        }
        return cardsInHand;
    }

    private static Set<IRailConnection> occupiedConnectionsFromJson(JsonObject playerStateJson) {
        Set<IRailConnection> occupiedConnections = new HashSet<>(
            occupiedConnectionForPlayer(
                playerStateJson.getAsJsonObject("this").get("acquired").getAsJsonArray()));
        return occupiedConnections;
    }

    private static Set<IRailConnection> occupiedConnectionForPlayer(JsonElement player) {
        Set<IRailConnection> occupiedConnections = new HashSet<>();
        for (JsonElement connection : player.getAsJsonArray()) {
            occupiedConnections.add(acquiredConnectionFromJson(connection));
        }
        return occupiedConnections;
    }

    public static IRailConnection acquiredConnectionFromJson(JsonElement connectionJson) {
        JsonArray jsonArray = connectionJson.getAsJsonArray();
        ICity city1 = new City(jsonArray.get(0).getAsString(), 0, 0);
        ICity city2 = new City(jsonArray.get(1).getAsString(), 0, 0);
        RailColor color = RailCardUtils.railColorFromLowercaseColor(jsonArray.get(2).getAsString());
        int length = jsonArray.get(3).getAsInt();
        return new RailConnection(new UnorderedPair<>(city1, city2), length, color);
    }
}
