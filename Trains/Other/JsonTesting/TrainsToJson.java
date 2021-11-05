package Other.JsonTesting;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import Other.City;
import Other.Connection;

/**
 * Class that contains methods to convert our own data types for a Trains game into JSON representations.
 */
public class TrainsToJson {

  /**
   * Converts a Connection to an Acquired Json representation [Name, Name, Color, Length]
   * where Name is a String, Color is a String, and Length is a natural
   * @param connection the Connection to be converted
   * @return the Acquired Json representation of the Connection
   */
  public static JsonArray connectionToAcquiredJson(Connection connection) {
    JsonArray acquired = new JsonArray();
    List<City> connectionCities = new ArrayList<>(connection.getCities());
    Collections.sort(connectionCities);
    acquired.add(new JsonPrimitive(connectionCities.get(0).getName()));
    acquired.add(new JsonPrimitive(connectionCities.get(1).getName()));
    acquired.add(new JsonPrimitive(connection.getConnectionColor().toString()));
    acquired.add(new JsonPrimitive(connection.getLength()));
    return acquired;
  }
}
