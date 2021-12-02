import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import action.AcquireConnectionAction;
import action.DrawCardsAction;
import action.TurnAction;
import map.City;
import map.Destination;
import map.ICity;
import map.IRailConnection;
import map.ITrainMap;
import map.MapDimensions;
import map.RailColor;
import map.RailConnection;
import map.TrainMap;
import utils.UnorderedPair;
import utils.json.ToJsonConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestToJsonConverter {

  ICity boston;
  ICity nyc;
  ICity chicago;
  ICity washington;
  ICity texas;
  ICity lincoln;

  IRailConnection bostonNYC;
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
    rails.add(chicagoNYC);
    rails.add(washingtonNYC);
    rails.add(texasNYC);
    rails.add(lincolnNYC);
    rails.add(bostonLincoln);
    rails.add(lincolnTexas);

    map = new TrainMap(cities, rails);
  }

  @Test
  public void testMapToJsonDefaultDimensions() {
    JsonObject expected = new JsonObject();
    expected.add("width", new JsonPrimitive(400));
    expected.add("height", new JsonPrimitive(400));
    JsonArray jsonCities = new JsonArray();
    for (ICity city : cities) {
      jsonCities.add(ToJsonConverter.cityToJson(city, 400, 400));
    }
    expected.add("cities", jsonCities);
    expected.add("connections", ToJsonConverter.connectionsToJson(rails));
    assertEquals(expected, ToJsonConverter.mapToJson(map));
  }

  @Test
  public void testMapToJsonCustomDimensions() {
    ITrainMap customDimensionMap = new TrainMap(cities, rails, new MapDimensions(300, 250));
    JsonObject expected = new JsonObject();
    expected.add("width", new JsonPrimitive(300));
    expected.add("height", new JsonPrimitive(250));
    JsonArray jsonCities = new JsonArray();
    for (ICity city : cities) {
      jsonCities.add(ToJsonConverter.cityToJson(city, 300, 250));
    }
    expected.add("cities", jsonCities);
    expected.add("connections", ToJsonConverter.connectionsToJson(rails));
    assertEquals(expected, ToJsonConverter.mapToJson(customDimensionMap));
  }

  @Test
  public void testConnectionsToJson() {
    JsonObject expected = new JsonObject();
    JsonObject nycChicagoSegment = new JsonObject();
    nycChicagoSegment.add("red", new JsonPrimitive(4));

    JsonObject nycTexasSegment = new JsonObject();
    nycTexasSegment.add("white", new JsonPrimitive(5));

    JsonObject nycLincolnSegment = new JsonObject();
    nycLincolnSegment.add("green", new JsonPrimitive(4));

    JsonObject nycWashingtonSegment = new JsonObject();
    nycWashingtonSegment.add("white", new JsonPrimitive(4));

    JsonObject nycTargets = new JsonObject();
    nycTargets.add("chicago", nycChicagoSegment);
    nycTargets.add("texas", nycTexasSegment);
    nycTargets.add("lincoln", nycLincolnSegment);
    nycTargets.add("washington", nycWashingtonSegment);

    JsonObject lincolnTexasSegment = new JsonObject();
    lincolnTexasSegment.add("red", new JsonPrimitive(3));

    JsonObject lincolnTargets = new JsonObject();
    lincolnTargets.add("texas", lincolnTexasSegment);

    JsonObject bostonNYCSegment = new JsonObject();
    bostonNYCSegment.add("blue", new JsonPrimitive(3));

    JsonObject bostonLincolnSegment = new JsonObject();
    bostonLincolnSegment.add("blue", new JsonPrimitive(3));

    JsonObject bostonTargets = new JsonObject();
    bostonTargets.add("NYC", bostonNYCSegment);
    bostonTargets.add("lincoln", bostonLincolnSegment);

    expected.add("NYC", nycTargets);
    expected.add("lincoln", lincolnTargets);
    expected.add("Boston", bostonTargets);

    assertEquals(expected, ToJsonConverter.connectionsToJson(rails));
  }

  @Test
  public void testCityToJson() {
    JsonArray expected = new JsonArray();
    expected.add(new JsonPrimitive("Boston"));

    JsonArray expectedCoords = new JsonArray();
    expectedCoords.add(new JsonPrimitive(200));
    expectedCoords.add(new JsonPrimitive(100));

    expected.add(expectedCoords);

    assertEquals(expected, ToJsonConverter.cityToJson(this.boston, 400, 400));
  }

  @Test
  public void testRailCardsToJson() {
    JsonArray expected = new JsonArray();
    expected.add(new JsonPrimitive("blue"));
    expected.add(new JsonPrimitive("blue"));
    expected.add(new JsonPrimitive("blue"));
    expected.add(new JsonPrimitive("blue"));
    expected.add(new JsonPrimitive("white"));
    expected.add(new JsonPrimitive("blue"));
    expected.add(new JsonPrimitive("green"));
    expected.add(new JsonPrimitive("green"));
    expected.add(new JsonPrimitive("red"));
    expected.add(new JsonPrimitive("red"));

    assertEquals(expected, ToJsonConverter.railCardsToJson(TestTrainsReferee.TenCardDeckSupplier()));
  }

  @Test
  public void testDestinationsToJson() {
    Destination bostonNYC = new Destination(boston, nyc);
    Destination lincolnWashington = new Destination(new UnorderedPair<ICity>(lincoln, washington));
    Destination texasChicago = new Destination(texas, chicago);

    Set<Destination> destinations = new HashSet<>();
    destinations.add(bostonNYC);
    destinations.add(lincolnWashington);
    destinations.add(texasChicago);

    JsonArray expected = new JsonArray();
    JsonArray jsonBostonNYC = new JsonArray();
    jsonBostonNYC.add(new JsonPrimitive("Boston"));
    jsonBostonNYC.add(new JsonPrimitive("NYC"));

    JsonArray jsonLincolnWashington = new JsonArray();
    jsonLincolnWashington.add(new JsonPrimitive("lincoln"));
    jsonLincolnWashington.add(new JsonPrimitive("washington"));

    JsonArray jsonTexasChicago = new JsonArray();
    jsonTexasChicago.add(new JsonPrimitive("texas"));
    jsonTexasChicago.add(new JsonPrimitive("chicago"));

    expected.add(jsonBostonNYC);
    expected.add(jsonLincolnWashington);
    expected.add(jsonTexasChicago);

    assertEquals(expected, ToJsonConverter.destinationsToJson(destinations));
  }

  @Test
  public void testDestinationToJson() {
    Destination lincolnWashington = new Destination(new UnorderedPair<ICity>(lincoln, washington));

    JsonArray expected = new JsonArray();
    expected.add(new JsonPrimitive("lincoln"));
    expected.add(new JsonPrimitive("washington"));

    assertEquals(expected, ToJsonConverter.destinationToJson(lincolnWashington));
  }

  @Test
  public void testTurnActionToJsonMoreCards() {
    TurnAction moreCards = new DrawCardsAction();
    assertEquals(new JsonPrimitive("more cards"), ToJsonConverter.turnActionToJSON(moreCards));
  }

  @Test
  public void testTurnActionToJsonAcquireConnection() {
    TurnAction acquireAction = new AcquireConnectionAction(bostonNYC);
    JsonArray expected = new JsonArray();
    expected.add(new JsonPrimitive("Boston"));
    expected.add(new JsonPrimitive("NYC"));
    expected.add(new JsonPrimitive("blue"));
    expected.add(new JsonPrimitive(3));
    assertEquals(expected, ToJsonConverter.turnActionToJSON(acquireAction));
  }

  @Test
  public void testRailConnectionToJson() {
    JsonArray expected = new JsonArray();
    expected.add(new JsonPrimitive("Boston"));
    expected.add(new JsonPrimitive("NYC"));
    expected.add(new JsonPrimitive("blue"));
    expected.add(new JsonPrimitive(3));
    assertEquals(expected, ToJsonConverter.railConnectionToJSON(bostonNYC));
  }

  @Test
  public void testAcquiredConnectionsToJson() {
    Set<IRailConnection> acquiredConnections = new HashSet<>();
    acquiredConnections.add(lincolnNYC);
    acquiredConnections.add(bostonNYC);
    acquiredConnections.add(washingtonNYC);

    JsonArray lincolnNYCJson = new JsonArray();
    lincolnNYCJson.add(new JsonPrimitive("NYC"));
    lincolnNYCJson.add(new JsonPrimitive("lincoln"));
    lincolnNYCJson.add(new JsonPrimitive("green"));
    lincolnNYCJson.add(new JsonPrimitive(4));

    JsonArray bostonNYCJson = new JsonArray();
    bostonNYCJson.add(new JsonPrimitive("Boston"));
    bostonNYCJson.add(new JsonPrimitive("NYC"));
    bostonNYCJson.add(new JsonPrimitive("blue"));
    bostonNYCJson.add(new JsonPrimitive(3));

    JsonArray washingtonNYCJson = new JsonArray();
    washingtonNYCJson.add(new JsonPrimitive("NYC"));
    washingtonNYCJson.add(new JsonPrimitive("washington"));
    washingtonNYCJson.add(new JsonPrimitive("white"));
    washingtonNYCJson.add(new JsonPrimitive(4));

    JsonArray expected = new JsonArray();
    expected.add(lincolnNYCJson);
    expected.add(bostonNYCJson);
    expected.add(washingtonNYCJson);

    assertEquals(expected, ToJsonConverter.acquiredConnectionsToJson(acquiredConnections));
  }

  @Test
  public void testPlayerGameStateToJson() {

  }

}
