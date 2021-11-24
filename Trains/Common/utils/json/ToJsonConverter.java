package utils.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import map.City;
import map.ICity;
import map.IRailConnection;
import map.ITrainMap;
import utils.ComparatorUtils;
import utils.OrderedPair;
import utils.UnorderedPair;

import java.util.Set;

public class ToJsonConverter {

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

    public static JsonObject connectionsToJson(Set<IRailConnection> connections) {

        JsonObject jsonConnections = new JsonObject();

        for (IRailConnection connection : connections) {
            UnorderedPair<ICity> connectionCities = connection.getCities();
            OrderedPair<ICity> citiesOrdered = ComparatorUtils.fromUnordered(connectionCities);

        }

    }

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
