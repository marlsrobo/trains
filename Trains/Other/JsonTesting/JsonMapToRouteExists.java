package Other.JsonTesting;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonStreamParser;

import Common.TrainsMap;
import Other.Utils;

/**
 * Outputs the result of whether 2 cities are connected by a route in a Map (cities and Map
 * represented by json in STDIN) to STDOUT
 */
public class JsonMapToRouteExists {

  /**
   * Outputs a boolean to STDOUT determining whether the cities specified are connected by a route
   * in the map (both provided in json)
   * @param args args to main (never used)
   */
  public static void main(String[] args) {
    // parse JSON string
    JsonStreamParser parsedJson = new JsonStreamParser(Utils.getJson());

    // getting the city names to see if they have a route between them.
    String city1Name = parsedJson.next().getAsString();
    String city2Name = parsedJson.next().getAsString();

    // getting the map and converting to our data representation
    TrainsMap map = JsonToTrains.jsonToMap(parsedJson.next().getAsJsonObject());

    // determining if there's a route between the cities in this map
    boolean routeExists = map.routeExistsBetweenCities(city1Name, city2Name);
    JsonPrimitive routeExistsJson = new JsonPrimitive(routeExists);

    // outputting the result to stdout
    System.out.println(routeExistsJson);
  }
}
