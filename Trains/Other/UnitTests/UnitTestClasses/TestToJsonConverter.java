import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import action.AcquireConnectionAction;
import action.DrawCardsAction;
import action.TurnAction;
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
import map.MapDimensions;
import map.RailColor;
import map.RailConnection;
import map.TrainMap;
import referee.GameEndReport;
import referee.game_state.IPlayerData;
import referee.game_state.PlayerData;
import referee.game_state.TrainsPlayerHand;
import tournament_manager.TournamentResult;
import utils.UnorderedPair;
import utils.json.ToJsonConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
  public void testRailCardsToJsonArray() {
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

    assertEquals(expected, ToJsonConverter.railCardsToJsonArray(TestTrainsReferee.TenCardDeckSupplier()));
  }

  @Test
  public void testRailCardsToJsonObject() {

    Map<RailCard, Integer> cards = new HashMap<>();
    cards.put(RailCard.BLUE, 5);
    cards.put(RailCard.RED, 10);
    cards.put(RailCard.GREEN, 12);
    cards.put(RailCard.WHITE, 2);

    JsonObject expected = new JsonObject();
    expected.add("blue", new JsonPrimitive(5));
    expected.add("red", new JsonPrimitive(10));
    expected.add("green", new JsonPrimitive(12));
    expected.add("white", new JsonPrimitive(2));

    assertEquals(expected, ToJsonConverter.railCardsToJsonObject(cards));
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
    expected.add(bostonNYCJson);
    expected.add(lincolnNYCJson);
    expected.add(washingtonNYCJson);

    assertTrue(ToJsonConverter.acquiredConnectionsToJson(acquiredConnections).contains(bostonNYCJson));
    assertTrue(ToJsonConverter.acquiredConnectionsToJson(acquiredConnections).contains(lincolnNYCJson));
    assertTrue(ToJsonConverter.acquiredConnectionsToJson(acquiredConnections).contains(washingtonNYCJson));
    assertEquals(3, ToJsonConverter.acquiredConnectionsToJson(acquiredConnections).size());
  }

  @Test
  public void testPlayerGameStateToJson() {
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

    IPlayerGameState gameState = new PlayerGameState(playerData, opponentInfo);

    JsonObject expected = new JsonObject();

    JsonObject thisObject = new JsonObject();

    JsonArray destination1 = ToJsonConverter.destinationToJson(lincolnWashington);
    JsonArray destination2 = ToJsonConverter.destinationToJson(texasChicago);

    thisObject.add("destination1", destination1);
    thisObject.add("destination2", destination2);
    thisObject.add("rails", new JsonPrimitive(10));
    thisObject.add("cards", ToJsonConverter.railCardsToJsonObject(gameState.getCardsInHand()));
    thisObject.add("acquired", ToJsonConverter.acquiredConnectionsToJson(acquiredConnections));

    expected.add("this", thisObject);

    JsonArray opponentsConnections = new JsonArray();
    JsonArray opponent1Array = ToJsonConverter.acquiredConnectionsToJson(opponent1Connections);
    JsonArray opponent2Array = ToJsonConverter.acquiredConnectionsToJson(opponent2Connections);
    opponentsConnections.add(opponent1Array);
    opponentsConnections.add(opponent2Array);

    expected.add("acquired", opponentsConnections);

    assertEquals(expected, ToJsonConverter.playerGameStateToJson(gameState));
  }

  @Test
  public void testTournamentResultToJson() {
    Set<String> winners = new HashSet<>();
    winners.add("Marley");
    winners.add("marley");
    winners.add("Ronan");
    Set<String> cheaters = new HashSet<>();
    cheaters.add("Bob");
    cheaters.add("Alice");
    TournamentResult result = new TournamentResult(winners, cheaters);

    JsonArray expected = new JsonArray();
    JsonArray expectedWinners = new JsonArray();
    expectedWinners.add(new JsonPrimitive("Marley"));
    expectedWinners.add(new JsonPrimitive("Ronan"));
    expectedWinners.add(new JsonPrimitive("marley"));
    expected.add(expectedWinners);

    JsonArray expectedCheaters = new JsonArray();
    expectedCheaters.add(new JsonPrimitive("Alice"));
    expectedCheaters.add(new JsonPrimitive("Bob"));
    expected.add(expectedCheaters);

    assertEquals(expected, ToJsonConverter.tournamentResultToJson(result));
  }

  @Test
  public void testGameReportToJson() {
    List<GameEndReport.PlayerScore> scores = new ArrayList<>();
    scores.add(new GameEndReport.PlayerScore("laura", 2));
    scores.add(new GameEndReport.PlayerScore("Lauren", 5));
    scores.add(new GameEndReport.PlayerScore("Marley", 20));
    scores.add(new GameEndReport.PlayerScore("Ronan", 20));

    Set<String> removedPlayers = new HashSet<>();
    removedPlayers.add("bruce");
    removedPlayers.add("harry");
    removedPlayers.add("Zorro");

    GameEndReport report = new GameEndReport(scores, removedPlayers);

    JsonArray expected = new JsonArray();
    JsonArray winnerRanks = new JsonArray();

    List<String> first = new ArrayList<>();
    first.add("Marley");
    first.add("Ronan");

    List<String> second = new ArrayList<>();
    second.add("Lauren");

    List<String> third = new ArrayList<>();
    third.add("laura");

    winnerRanks.add(ToJsonConverter.rankToJson(first));
    winnerRanks.add(ToJsonConverter.rankToJson(second));
    winnerRanks.add(ToJsonConverter.rankToJson(third));

    expected.add(winnerRanks);

    List<String> cheaterNames = new ArrayList<>();
    cheaterNames.add("Zorro");
    cheaterNames.add("bruce");
    cheaterNames.add("harry");

    expected.add(ToJsonConverter.rankToJson(cheaterNames));

    assertEquals(expected, ToJsonConverter.gameReportToJson(report));
  }

  @Test
  public void testGameReportToRanking() {
    List<GameEndReport.PlayerScore> scores = new ArrayList<>();
    scores.add(new GameEndReport.PlayerScore("Lauren", 5));
    scores.add(new GameEndReport.PlayerScore("laura", 2));
    scores.add(new GameEndReport.PlayerScore("Marley", 20));
    scores.add(new GameEndReport.PlayerScore("Ronan", 20));

    Set<String> removedPlayers = new HashSet<>();
    removedPlayers.add("bruce");
    removedPlayers.add("harry");
    removedPlayers.add("Zorro");

    GameEndReport report = new GameEndReport(scores, removedPlayers);

    List<List<String>> expected = new ArrayList<>();

    List<String> first = new ArrayList<>();
    first.add("Marley");
    first.add("Ronan");

    List<String> second = new ArrayList<>();
    second.add("Lauren");

    List<String> third = new ArrayList<>();
    third.add("laura");

    expected.add(first);
    expected.add(second);
    expected.add(third);

    assertEquals(expected, ToJsonConverter.gameReportToRanking(report));
  }

  @Test
  public void testRankToJson() {
    List<String> rank = new ArrayList<>();
    rank.add("Marley");
    rank.add("Adam");
    rank.add("adam");
    rank.add("Bruce");

    JsonArray expected = new JsonArray();
    expected.add(new JsonPrimitive("Adam"));
    expected.add(new JsonPrimitive("Bruce"));
    expected.add(new JsonPrimitive("Marley"));
    expected.add(new JsonPrimitive("adam"));

    assertEquals(expected, ToJsonConverter.rankToJson(rank));
  }

}
