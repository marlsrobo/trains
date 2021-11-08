package harnesses;

import com.google.gson.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import map.*;
import utils.UnorderedPair;

/**
 * Performs an integration test on the map for a game of trains by consuming a specification for a Trains game map, and
 * two cities on the map, then outputting whether the two cities are connected by any path.
 *
 * The specifications for the JSON input format can be found at:
 *      https://www.ccs.neu.edu/home/matthias/4500-f21/3.html
 *
 * Example input:
 *      "Texas"
 *      "Boston"
 *      {
 *          "width" : 800,
 *          "height": 800,
 *          "cities": [["Seattle", [0, 0]], ["Boston", [800, 50]], ["Texas", [500, 800]]],
 *          "connections": {"Boston": {"Seattle": {"red": 3},
 *                                      "Texas": {"green": 5}},
 *                          "Seattle": {"Texas": {"blue": 4}}}
 *      }
 */
public class XMap {
    /**
     * Performs the integration test consuming JSON input from stdin, and outputting the result to stdout.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        RunTest(new InputStreamReader(System.in), System.out);
    }

    /**
     * Performs the integration test consuming JSON input from the given reader, and outputting the result to the given
     * PrintStream.
     *
     * @param input A stream of three JSON values representing two cities on the map, and the game map for a game of
     *              Trains
     * @param output The output stream where a JSON boolean representing whether or not the two cities are connected
     *               will be written.
     */
    private static void RunTest(Reader input, PrintStream output) {
        JsonStreamParser parser = new JsonStreamParser(input);
        try (input) {
            JsonElement startCity = parser.next();
            JsonElement endCity = parser.next();
            JsonElement mapSpecification = parser.next();
            String startName = startCity.getAsString();
            String endName = endCity.getAsString();
            UnorderedPair<ICity> destination =
                new UnorderedPair<>(new City(startName, 0, 0), new City(endName, 0, 0));
            ITrainMap map = trainMapFromJson(mapSpecification);

            boolean result = map.getAllPossibleDestinations().contains(destination);
            output.println(result);
        } catch (JsonIOException | IOException ignored) {
        }
    }

    /**
     * Creates an ITrainMap from JSON input representing a map for the game Trains.
     *
     * The JSON input will be in the form:
     *      {
     *          "width" : 800,
     *          "height": 800,
     *          "cities": [["Seattle", [0, 0]], ["Boston", [800, 50]], ["Texas", [500, 800]]],
     *          "connections": {"Boston": {"Seattle": {"red": 3},
     *                                      "Texas": {"green": 5}},
     *                          "Seattle": {"Texas": {"blue": 4}}}
     *      }
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
     * Creates a map of city names to ICity objects for all of the cities specified in the JSON input.
     *
     * The JSON input will be in the form:
     *      [["Seattle", [0, 0]], ["Boston", [800, 50]], ["Texas", [500, 800]]]
     *
     * @param mapWidth The width of the game map in pixels
     * @param mapHeight The height of the game map in pixels
     * @param citiesSpecification The JSON specifying a list of cities
     * @return A map of city names to ICity objects for all of the cities specified in the JSON input
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
     * Creates a list of IRailConnection objects for all of the connections specified in the JSON input.
     *
     * The JSON input will be in the form:
     *      {"Boston": {"Seattle": {"red": 3},
     *                  "Texas": {"green": 5}},
     *       "Seattle": {"Texas": {"blue": 4}}}
     *
     * @param connectionsSpecification The JSON specifying the connections
     * @param cities A map of city names to ICity objects for all cities in the map
     * @return A list of IRailConnection objects for all of the connections specified in the JSON input
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
     * Creates a list of IRailConnection objects for all of the connections from one city specified in the JSON input.
     *
     * The JSON input will be in the form:
     *      {"Seattle": {"red": 3},
     *       "Texas": {"green": 5}}
     * Representing that the given city is connected to Seattle by a red connection with length 3, and to Texas by a
     * green connection of length 5.
     *
     * @param targets The JSON specifying connections from the given city to other cities
     * @param cities A map of city names to ICity objects for all cities in the map
     * @param startCity The city that is the source of all connections specified in targets
     * @return A list of IRailConnection objects for all of the connections specified in the JSON input
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
     * Parses the JSON 'Segment' object to calculate the specified direct connections between two cities.
     * @param segment the JSON specifying the connections between two cities.
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
}
