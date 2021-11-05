import Other.*;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import Common.PlayerGameState;
import Common.TrainsMap;

import static junit.framework.TestCase.assertEquals;

/**
 * Unit tests for PlayerGameState.
 */
public class PlayerGameStateTests {
  TestUtils utils = new TestUtils();

  private TrainsMap map;
  private List<Integer> orderOfPlayerTurns;
  private int currentPlayer;
  Map<Connection, Integer> connectionsStatus;

  PlayerInventory inventory1;
  PlayerGameState playerGameState1;

  @Before
  public void setup() {
    map = utils.map;
    orderOfPlayerTurns = utils.playerTurns;
    currentPlayer = orderOfPlayerTurns.get(0);
    connectionsStatus = utils.createConnectionsStatus(-1);
    Map<ConnectionColor, Integer> playerCards = new HashMap<>();
    for (ConnectionColor color : ConnectionColor.values()) {
      playerCards.put(color, 10);
    }

    Set<Destination> destinations = new HashSet<>();
    destinations.add(new Destination(TestUtils.boston, TestUtils.newYork));
    destinations.add(new Destination(TestUtils.newYork, TestUtils.houston));

    this.inventory1 = new PlayerInventory(playerCards, 40, destinations);

    playerGameState1 = new PlayerGameState(map, orderOfPlayerTurns, currentPlayer, connectionsStatus, inventory1);
  }

  @Test
  public void testGetMap() {
    assertEquals(this.map, playerGameState1.getMap());
  }

  @Test
  public void testGetConnectionsStatus() {
    assertEquals(this.connectionsStatus, playerGameState1.getConnectionsStatus());
  }

  @Test
  public void testGetCardsInHand() {
    assertEquals(this.inventory1.getCardsInHand(), playerGameState1.getCardsInHand());
  }

  @Test
  public void testGetRailsInHand() {
    assertEquals(40, playerGameState1.getRailsInHand());
  }

  @Test
  public void testGetDestinations() {
    Set<Destination> dests = new HashSet<>();
    dests.add(new Destination(TestUtils.houston, TestUtils.newYork));
    dests.add(new Destination(TestUtils.boston, TestUtils.newYork));
    assertEquals(dests, playerGameState1.getDestinations());
  }

  @Test
  public void testFindUnacquiredConnectionsAllUnaquired() {
    assertEquals(map.getConnections(), playerGameState1.findUnaquiredConnections());
  }

  @Test
  public void testFindUnacquiredConnectionsSomeAcquired() {
    connectionsStatus.put(TestUtils.bostonNYCRed3, 1);
    connectionsStatus.put(TestUtils.bostonMiamiWhite3, -332);

    PlayerGameState gameState = new PlayerGameState(map, orderOfPlayerTurns, currentPlayer, connectionsStatus,
            inventory1);

    Set<Connection> unaquiredConnections = new HashSet<>();
    unaquiredConnections.add(TestUtils.losAngelesNYCBlue5);
    unaquiredConnections.add(TestUtils.bostonNYCGreen4);
    unaquiredConnections.add(TestUtils.miamiLAWhite3);
    assertEquals(unaquiredConnections, gameState.findUnaquiredConnections());
  }

  @Test
  public void testFindUnacquiredConnectionsAllAcquired() {
    connectionsStatus.put(TestUtils.bostonNYCRed3, 1);
    connectionsStatus.put(TestUtils.bostonMiamiWhite3, -332);
    connectionsStatus.put(TestUtils.losAngelesNYCBlue5, 23);
    connectionsStatus.put(TestUtils.bostonNYCGreen4, -78);
    connectionsStatus.put(TestUtils.miamiLAWhite3, 0);

    PlayerInventory inventoryNew = new PlayerInventory(inventory1.getCardsInHand(),
            30, inventory1.getDestinations());

    PlayerGameState gameState = new PlayerGameState(map, orderOfPlayerTurns, currentPlayer,
            connectionsStatus, inventoryNew);

    Set<Connection> unaquiredConnections = new HashSet<>();

    assertEquals(unaquiredConnections, gameState.findUnaquiredConnections());
  }
}
