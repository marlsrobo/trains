package utils.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import map.ICity;
import map.IRailConnection;
import map.ITrainMap;
import utils.ComparatorUtils;
import utils.OrderedPair;
import utils.UnorderedPair;

import java.util.Set;

/**
 * Util class to convert our data representations to their equivalent JSON representations.
 */
public class ToJsonConverter {

    /**
     * Converts an ITrainsMap to JSON
     * @param map the ITrainsMap
     * @return the JSON representation of the ITrainsMaps
     */
    public static JsonObject mapToJson(ITrainMap map) {
        int width = map.getMapDimension().getWidth();
        int height = map.getMapDimension().getHeight();

        Set<ICity> cities = map.getCities();
        JsonArray jsonCities = new JsonArray();

        for (ICity city : cities) {
            jsonCities.add(cityToJson(city, width, height));
        }

        JsonObject jsonConnections = connectionsToJson(map.getRailConnections());

        JsonObject jsonMap = new JsonObject();
        jsonMap.add("width", new JsonPrimitive(width));
        jsonMap.add("height", new JsonPrimitive(height));
        jsonMap.add("cities", jsonCities);
        jsonMap.add("connections", jsonConnections);

        return jsonMap;
    }

    /**
     * Converts a set of IRailConnections to JSON
     * @param connections the IRailConnections
     * @return the JSON representation of the IRailConnections
     */
    public static JsonObject connectionsToJson(Set<IRailConnection> connections) {

        JsonObject jsonConnections = new JsonObject();

        for (IRailConnection connection : connections) {
            UnorderedPair<ICity> connectionCities = connection.getCities();
            OrderedPair<ICity> citiesOrdered = ComparatorUtils.fromUnordered(connectionCities);
            String sourceName = citiesOrdered.first.getName();
            String targetName = citiesOrdered.second.getName();

            JsonObject segment = new JsonObject();
            segment
                .add(connection.getColor().toString(), new JsonPrimitive(connection.getLength()));

            if (jsonConnections.keySet().contains(sourceName)) {
                if (jsonConnections.getAsJsonObject(sourceName).keySet().contains(targetName)) {
                    jsonConnections.getAsJsonObject(sourceName).getAsJsonObject(targetName)
                        .add(connection.getColor().toString(),
                            new JsonPrimitive(connection.getLength()));
                } else {
                    jsonConnections.getAsJsonObject(sourceName).add(targetName, segment);
                }
            } else {
                JsonObject target = new JsonObject();
                target.add(targetName, segment);

                jsonConnections.add(sourceName, target);
            }
        }

        return jsonConnections;
    }

    /**
     * Converts an ICity to JSON
     * @param city the ICity
     * @param mapWidth the width of the ITrainsMap that the city is a part of
     * @param mapHeight the height of the ITrainsMap that the city is a part of
     * @return the JSON representation of the ICity
     */
    public static JsonArray cityToJson(ICity city, int mapWidth, int mapHeight) {
        String name = city.getName();
        Double xCoord = city.getRelativePosition().first;
        Double yCoord = city.getRelativePosition().second;

        int xCoordAbsolute = (int) (xCoord * mapWidth);
        int yCoordAbsolute = (int) (yCoord * mapHeight);

        JsonArray coords = new JsonArray();
        coords.add(xCoordAbsolute);
        coords.add(yCoordAbsolute);

        JsonArray jsonCity = new JsonArray();
        jsonCity.add(name);
        jsonCity.add(coords);

        return jsonCity;
    }
}
