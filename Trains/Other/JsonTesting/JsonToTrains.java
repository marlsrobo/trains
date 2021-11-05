package Other.JsonTesting;

import Other.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Common.PlayerGameState;
import Common.TrainsMap;

/**
 * Class that contains methods to parse JSON into our own data types for a Trains game.
 */
public class JsonToTrains {

  /**
   * Returns a Trains Map which is parsed from the passed in JsonObject
   * @param jsonMap the JsonObject representing a Map in json
   * @return the Map which was parsed from the JsonObject
   */
  public static TrainsMap jsonToMap(JsonObject jsonMap) {
    int mapWidth = jsonMap.get("width").getAsInt();
    int mapHeight = jsonMap.get("height").getAsInt();
    JsonArray mapCities = jsonMap.get("cities").getAsJsonArray();
    JsonObject mapConnections = jsonMap.get("connections").getAsJsonObject();

    // converting to our cities and connections
    Map<String, City> cities = jsonToCities(mapCities, mapWidth, mapHeight);
    Set<Connection> connections = jsonToConnections(mapConnections, cities);

    // making our map
    return new TrainsMap(new HashSet<>(cities.values()), connections, mapWidth, mapHeight);
  }

  /***
   * Converts a JsonArray of cities to our internal structure for cities in a Map.
   * @param mapCities JsonArray of cities
   * @return a Map<String, City> where String is the city name, to be used in our Map.
   */
  private static Map<String, City> jsonToCities(JsonArray mapCities, int mapWidth, int mapHeight) {
    // getting all the cities and converting them into a Set<City>
    // to match our Map cities field
    Map<String, City> cities = new HashMap<>();
    for (JsonElement c : mapCities) {
      JsonArray jsonCity = c.getAsJsonArray();
      String cityName = jsonCity.get(0).getAsString();
      JsonArray jsonCoordinates = jsonCity.get(1).getAsJsonArray();

      double cityX = jsonCoordinates.get(0).getAsDouble();
      double cityY = jsonCoordinates.get(1).getAsDouble();

      cityX = Utils.absoluteToRelativeCoord(cityX, mapWidth);
      cityY = Utils.absoluteToRelativeCoord(cityY, mapHeight);

      City city = new City(cityName, cityX, cityY);
      cities.put(cityName, city);
    }
    return cities;
  }

  /***
   * Converts a JsonObject of connections to our representation for connections in a Map
   * @param mapConnections a JsonObject representing connections
   * @return a Set<Connection> to be used in our Map.
   */
  private static Set<Connection> jsonToConnections(JsonObject mapConnections, Map<String, City> cities) {
    // getting the connections from the JSON
    Set<Connection> connections = new HashSet<>();
    Set<String> connectionOrigins = mapConnections.keySet();
    // looping through the names of the source cities
    for (String origin : connectionOrigins) {
      City originCity = cities.get(origin);

      JsonObject targets = mapConnections.get(origin).getAsJsonObject();

      Set<String> targetNames = targets.keySet();

      // looping through the name of the targets of the source cities
      for (String targetName : targetNames) {
        City targetCity = cities.get(targetName);

        Set<City> connectionCities = new HashSet<>();
        connectionCities.add(originCity);
        connectionCities.add(targetCity);

        JsonObject segments = targets.get(targetName).getAsJsonObject();
        Set<String> colors = segments.keySet();
        // looping through the segments of the targets
        for (String color : colors) {
          int length = segments.get(color).getAsInt();
          Connection connection = new Connection(ConnectionColor.stringToColor(color),
                  length, connectionCities);
          connections.add(connection);

        }
      }
    }
    return connections;
  }

  /**
   * Converts a JsonArray representation of a connection [Name, Name, Color, Length] in a trains
   * game to our representation of a Connection.
   * @param jsonConnection the JsonArray representation of a connection
   * @param map the TrainsMap that this Connection is within
   * @return the Connection
   */
  public static Connection jsonToAcquired(JsonArray jsonConnection, TrainsMap map) {
    String city1Name = jsonConnection.get(0).getAsString();
    String city2Name = jsonConnection.get(1).getAsString();
    String connectionColor = jsonConnection.get(2).getAsString();
    int length = jsonConnection.get(3).getAsInt();

    City city1 = map.getCityFromName(city1Name);
    City city2 = map.getCityFromName(city2Name);

    ConnectionColor color = ConnectionColor.stringToColor(connectionColor);

    return new Connection(color, length, city1, city2);
  }

  /**
   * Converts a JSON representation of a player game state to our internal representation as a
   * PlayerGameState
   * @param jsonPlayerGameState the JSON representation of the player's game state
   * @param map the TrainsMap that the player is referring to in the game
   * @return our representation of the PlayerGameState
   */
  public static PlayerGameState jsonToPlayerGameState(JsonObject jsonPlayerGameState, TrainsMap map) {
    JsonObject jsonPlayer = jsonPlayerGameState.getAsJsonObject("this");
    // get the acquired connections of all the other players
    JsonArray jsonAcquired = jsonPlayerGameState.getAsJsonArray("acquired");
    // get the acquired connections of this player
    JsonArray jsonPlayerAcquired = jsonPlayer.get("acquired").getAsJsonArray();
    // put all the acquired connections together
    jsonAcquired.add(jsonPlayerAcquired);
    // get the statuses of all the connections for the given map
    Map<Connection, Integer> connectionsStatus = jsonToConnectionsStatus(jsonAcquired, map);

    JsonArray playerDestination1 = jsonPlayer.get("destination1").getAsJsonArray();
    JsonArray playerDestination2 = jsonPlayer.get("destination2").getAsJsonArray();

    Destination destination1 = jsonToDestination(playerDestination1, map);
    Destination destination2 = jsonToDestination(playerDestination2, map);

    Set<Destination> destinations = new HashSet<>();
    destinations.add(destination1);
    destinations.add(destination2);

    int rails = jsonPlayer.get("rails").getAsInt();
    JsonObject cards = jsonPlayer.get("cards").getAsJsonObject();
    Map<ConnectionColor, Integer> cardsInHand = jsonToPlayerColoredCards(cards);

    int numPlayers = jsonAcquired.size();
    List<Integer> orderOfPlayerTurns = new ArrayList<>();
    for (int i = 0; i < numPlayers; i++) {
      orderOfPlayerTurns.add(i);
    }

    int currentPlayer = orderOfPlayerTurns.get(orderOfPlayerTurns.size() - 1);

    PlayerInventory inventory = new PlayerInventory(cardsInHand,
            rails, destinations);

    return new PlayerGameState(map, orderOfPlayerTurns, currentPlayer,
            connectionsStatus, inventory);
  }

  private static Destination jsonToDestination(JsonArray jsonDestination, TrainsMap map) {
    String city1Name = jsonDestination.get(0).getAsString();
    City city1 = map.getCityFromName(city1Name);

    String city2Name = jsonDestination.get(1).getAsString();
    City city2 = map.getCityFromName(city2Name);

    return new Destination(city1, city2);
  }

  /**
   * Converts JSON representation of connection statuses to our internal representation of
   * connection statuses within a Player Game State
   * @param jsonAcquiredConnections the json to be parsed containing the acquired connections
   * @param map the map that contains the corresponding connections
   * @return a Map of Connection to Integer representing the status of each connection
   *          - if acquired, Integer is the id of the player who has acquired it
   *          - if not acquired, Integer is RefereeGameState.NOT_ACQUIRED_CONNECTION_STATUS
   */
  private static Map<Connection, Integer> jsonToConnectionsStatus(JsonArray jsonAcquiredConnections,
                                                                  TrainsMap map) {
    HashMap<Connection, Integer> connectionStatus = new HashMap<>();
    int playerIndex = 0;

    // initialize all connections as unaquired
    Set<Connection> mapConnections = map.getConnections();
    for (Connection connection : mapConnections) {
      connectionStatus.put(connection, Utils.NOT_ACQUIRED_CONNECTION_STATUS);
    }

    // update connections' statuses to acquired with the specific player's id if it is acquired
    for (JsonElement player : jsonAcquiredConnections) {
      JsonArray jsonConnections = player.getAsJsonArray();
      for (JsonElement jsonConnection : jsonConnections) {
        JsonArray jsonConnectionArray = jsonConnection.getAsJsonArray();
        Connection connection = jsonToAcquired(jsonConnectionArray, map);
        connectionStatus.put(connection, playerIndex);
      }
      playerIndex++;
    }
    return connectionStatus;
  }

  /**
   * Converts JSON representation of a player's hand of colored cards to our internal
   * representation of cards
   * @param jsonCards the JSON object representing the colored cards
   * @return Map of ConnectionColor to Integer representing how many cards of each color
   */
  private static Map<ConnectionColor, Integer> jsonToPlayerColoredCards(JsonObject jsonCards) {
    Map<ConnectionColor, Integer> cards = new HashMap<>();
    // initialize the cards to contain all the colors with 0 for each
    for (ConnectionColor color : ConnectionColor.values()) {
      cards.put(color, 0);
    }
    // add the remaining card counts according to json player's cards
    for (String color : jsonCards.keySet()) {
      ConnectionColor cardColor = ConnectionColor.stringToColor(color);
      int numCards = jsonCards.get(color).getAsInt();
      cards.put(cardColor, numCards);
    }
    return cards;
  }
}
