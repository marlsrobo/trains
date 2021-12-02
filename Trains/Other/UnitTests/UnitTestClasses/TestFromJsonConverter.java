import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import game_state.IOpponentInfo;
import game_state.IPlayerGameState;
import game_state.OpponentInfo;
import game_state.PlayerGameState;
import game_state.RailCard;
import map.City;
import map.Destination;
import map.ICity;
import map.IRailConnection;
import map.ITrainMap;
import map.RailColor;
import map.RailConnection;
import map.TrainMap;
import referee.game_state.IPlayerData;
import referee.game_state.PlayerData;
import referee.game_state.TrainsPlayerHand;
import test_utils.TrainsMapUtils;
import utils.UnorderedPair;
import utils.json.FromJsonConverter;
import utils.json.ToJsonConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestFromJsonConverter {

  ICity boston;
  ICity nyc;
  ICity chicago;
  ICity washington;
  ICity texas;
  ICity lincoln;

  IRailConnection bostonNYC;
  IRailConnection bostonNYC2;
  IRailConnection chicagoNYC;
  IRailConnection washingtonNYC;
  IRailConnection texasNYC;
  IRailConnection lincolnNYC;
  IRailConnection bostonLincoln;
  IRailConnection lincolnTexas;

  Set<ICity> cities;
  Set<IRailConnection> rails;
  ITrainMap map;

  @BeforeEach
  public void init() {
    boston = new City("Boston", 0.5, 0.25);
    nyc = new City("NYC", 0, 0);
    chicago = new City("chicago", 0, 0);
    washington = new City("washington", 0, 0);
    texas = new City("texas", 0, 0);
    lincoln = new City("lincoln", 0, 0);
    cities = new HashSet<>();
    cities.add(boston);
    cities.add(nyc);
    cities.add(chicago);
    cities.add(washington);
    cities.add(texas);
    cities.add(lincoln);

    bostonNYC = new RailConnection(new UnorderedPair<>(boston, nyc), 3,
            RailColor.BLUE);
    bostonNYC2 = new RailConnection(new UnorderedPair<>(boston, nyc), 4,
            RailColor.RED);
    chicagoNYC = new RailConnection(new UnorderedPair<>(chicago, nyc), 4,
            RailColor.RED);
    washingtonNYC = new RailConnection(new UnorderedPair<>(washington, nyc), 4,
            RailColor.WHITE);
    texasNYC = new RailConnection(new UnorderedPair<>(texas, nyc), 5,
            RailColor.WHITE);
    lincolnNYC = new RailConnection(new UnorderedPair<>(lincoln, nyc), 4,
            RailColor.GREEN);
    bostonLincoln = new RailConnection(new UnorderedPair<>(boston, lincoln), 3,
            RailColor.BLUE);
    lincolnTexas = new RailConnection(new UnorderedPair<>(lincoln, texas), 3,
            RailColor.RED);
    rails = new HashSet<>();
    rails.add(bostonNYC);
    rails.add(bostonNYC2);
    rails.add(chicagoNYC);
    rails.add(washingtonNYC);
    rails.add(texasNYC);
    rails.add(lincolnNYC);
    rails.add(bostonLincoln);
    rails.add(lincolnTexas);

    map = new TrainMap(cities, rails);
  }

  @Test
  public void testFromJsonToUnvalidatedSetOfDestinationsValidJson() {
    JsonArray jsonDestinations = new JsonArray();
    JsonArray destination1Json = new JsonArray();
    destination1Json.add(new JsonPrimitive("Boston"));
    destination1Json.add(new JsonPrimitive("NYC"));

    JsonArray destination2Json = new JsonArray();
    destination2Json.add(new JsonPrimitive("Seattle"));
    destination2Json.add(new JsonPrimitive("NYC"));

    jsonDestinations.add(destination1Json);
    jsonDestinations.add(destination2Json);

    Set<UnorderedPair<String>> expected = new HashSet<>();
    UnorderedPair<String> destination1 = new UnorderedPair<String>("Boston", "NYC");
    UnorderedPair<String> destination2 = new UnorderedPair<String>("Seattle", "NYC");
    expected.add(destination1);
    expected.add(destination2);

    assertEquals(expected, FromJsonConverter.fromJsonToUnvalidatedSetOfDestinations(jsonDestinations));
  }

  @Test
  public void testFromJsonToUnvalidatedSetOfDestinationsInvalidJson() {

    String jsonString = "{\"Boston\": \"NYC\", \"Seattle\": \"NYC\"}";
    JsonElement json = new JsonParser().parse(jsonString);

    assertThrows(IllegalArgumentException.class,
            () -> FromJsonConverter.fromJsonToUnvalidatedSetOfDestinations(json));
  }

  @Test
  public void testFromJsonToUnvalidatedDestinationValidJson() {
    JsonArray destination1Json = new JsonArray();
    destination1Json.add(new JsonPrimitive("Boston"));
    destination1Json.add(new JsonPrimitive("NYC"));
    UnorderedPair<String> destination1 = new UnorderedPair<String>("Boston", "NYC");

    assertEquals(destination1, FromJsonConverter.fromJsonToUnvalidatedDestination(destination1Json));
  }

  @Test
  public void testTrainMapFromJsonValidJson() {
    JsonObject jsonMap = ToJsonConverter.mapToJson(this.map);
    assertTrue(TrainsMapUtils.sameMap(this.map, FromJsonConverter.trainMapFromJson(jsonMap)));
  }

  @Test
  public void testGetCitiesFromJsonValidJson() {
    JsonArray jsonCities = new JsonArray();
    Map<String, ICity> expected = new HashMap<>();

    for (ICity city : this.cities) {
      jsonCities.add(ToJsonConverter.cityToJson(city, 200, 300));
      expected.put(city.getName(), city);
    }

    assertEquals(expected, FromJsonConverter.getCitiesFromJson(200, 300, jsonCities));
  }

  @Test
  public void testGetRailConnectionsFromJsonValidJson() {

    Map<String, ICity> citiesMap = new HashMap<>();

    for (ICity city : this.cities) {
      citiesMap.put(city.getName(), city);
    }

    JsonObject jsonConnections = ToJsonConverter.connectionsToJson(this.rails);

    assertTrue(this.rails.containsAll(FromJsonConverter.getRailConnectionsFromJson(jsonConnections, citiesMap)));
    assertTrue(FromJsonConverter.getRailConnectionsFromJson(jsonConnections, citiesMap).containsAll(this.rails));
    assertEquals(this.rails.size(), FromJsonConverter.getRailConnectionsFromJson(jsonConnections, citiesMap).size());
  }

  @Test
  public void testParseConnectionsFromCityValidJson() {

    JsonObject segment = new JsonObject();
    segment.add("blue", new JsonPrimitive(3));
    segment.add("red", new JsonPrimitive(4));
    UnorderedPair<ICity> endpoints = new UnorderedPair<>(this.boston, this.nyc);

    List<IRailConnection> expected = new ArrayList<>();
    expected.add(this.bostonNYC);
    expected.add(this.bostonNYC2);

    assertEquals(expected, FromJsonConverter.parseConnectionsForPairOfCities(segment, endpoints));
  }

  @Test
  public void testCardsFromJsonValidJson() {
    JsonArray jsonCards = new JsonArray();
    jsonCards.add("blue");
    jsonCards.add("blue");
    jsonCards.add("blue");
    jsonCards.add("blue");
    jsonCards.add("white");
    jsonCards.add("blue");
    jsonCards.add("green");
    jsonCards.add("green");
    jsonCards.add("red");
    jsonCards.add("red");

    List<RailCard> expected = TestTrainsReferee.TenCardDeckSupplier();
    assertEquals(expected, FromJsonConverter.cardsFromJson(jsonCards));
  }

  @Test
  public void testPlayerStateFromJsonValidJson() {
    Destination lincolnWashington = new Destination(new UnorderedPair<ICity>(lincoln, washington));
    Destination texasChicago = new Destination(texas, chicago);

    Set<Destination> destinations = new HashSet<>();
    destinations.add(lincolnWashington);
    destinations.add(texasChicago);

    Set<IRailConnection> acquiredConnections = new HashSet<>();
    acquiredConnections.add(texasNYC);
    acquiredConnections.add(bostonLincoln);

    IPlayerData playerData = new PlayerData(new TrainsPlayerHand(TestTrainsReferee.TenCardDeckSupplier()),
            10, destinations, acquiredConnections);

    List<IOpponentInfo> opponentInfo = new ArrayList<>();

    Set<IRailConnection> opponent1Connections = new HashSet<>();
    opponent1Connections.add(bostonNYC);
    IOpponentInfo opponent1 = new OpponentInfo(opponent1Connections);
    opponentInfo.add(opponent1);

    Set<IRailConnection> opponent2Connections = new HashSet<>();
    opponent2Connections.add(washingtonNYC);
    IOpponentInfo opponent2 = new OpponentInfo(opponent2Connections);
    opponentInfo.add(opponent2);

    IPlayerGameState expected = new PlayerGameState(playerData, opponentInfo);

    JsonObject jsonPlayerState = ToJsonConverter.playerGameStateToJson(expected);

    assertTrue(samePlayerGameState(expected, FromJsonConverter.playerStateFromJson(jsonPlayerState, this.map)));
  }

  private boolean samePlayerGameState(IPlayerGameState gameState1, IPlayerGameState gameState2) {
    boolean sameCards = gameState1.getCardsInHand().equals(gameState2.getCardsInHand());
    System.out.println("1 cards: " + gameState1.getCardsInHand().toString());
    System.out.println("2 cards: " + gameState2.getCardsInHand().toString());
    boolean sameDestinations = gameState1.getDestinations().equals(gameState2.getDestinations());
    System.out.println("1 destinations: " + gameState1.getDestinations().toString());
    System.out.println("2 destinations: " + gameState2.getDestinations().toString());
    boolean sameRails = gameState1.getNumRails() == gameState2.getNumRails();
    System.out.println("1 rails: " + gameState1.getNumRails());
    System.out.println("2 rails: " + gameState2.getNumRails());
    boolean sameOwnedConnections = gameState1.getOwnedConnections().equals(gameState2.getOwnedConnections());
    System.out.println("1 connections: " + gameState1.getOwnedConnections().toString());
    System.out.println("2 connections: " + gameState2.getOwnedConnections().toString());
    boolean sameOpponentInfo = gameState1.getOpponentInfo().equals(gameState2.getOpponentInfo());
    System.out.println("1 opponent info: " + gameState1.getOpponentInfo().toString());
    System.out.println("2 opponent info: " + gameState2.getOpponentInfo().toString());
    return sameCards && sameDestinations && sameRails && sameOwnedConnections && sameOpponentInfo;
  }

}
