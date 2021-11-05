import Common.PlayerGameState;
import Common.TrainsMap;
import Other.*;
import Other.Strategies.BuyNowStrategy;
import Other.Strategies.Hold10Strategy;
import Player.Strategy;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.*;

import static junit.framework.TestCase.*;

/**
 * Unit tests for Strategy.
 * Hold-10 vs. BuyNow
 */
public class StrategiesTests {
    @Rule
    public final ExpectedException expectedEx = ExpectedException.none();

    TestUtils utils = new TestUtils();

    Strategy hold10 = new Hold10Strategy();
    Strategy buyNow = new BuyNowStrategy();

    private TrainsMap map;
    private List<Integer> orderOfPlayerTurns;
    private int currentPlayer;
    public Map<ConnectionColor, Integer> playerCards;
    Map<Connection, Integer> connectionsStatus;
    Map<Connection, Integer> connectionsStatus2;

    Set<Destination> destinations;

    PlayerGameState playerGameState1;
    PlayerGameState playerGameState2;

    Set<Destination> destinationsToPickFrom;

    @Before
    public void setup() {
        map = utils.map;
        orderOfPlayerTurns = utils.playerTurns;
        currentPlayer = orderOfPlayerTurns.get(0);
        connectionsStatus = utils.createConnectionsStatus(-1);
        connectionsStatus2 = utils.createConnectionsStatus(1);


        playerCards = new HashMap<>();
        for (ConnectionColor color : ConnectionColor.values()) {
            playerCards.put(color, 10);
        }
        destinations = new HashSet<>();
        destinations.add(new Destination(TestUtils.boston, TestUtils.newYork));
        destinations.add(new Destination(TestUtils.newYork, TestUtils.houston));

        PlayerInventory inventory1 = new PlayerInventory(playerCards,
                40, destinations);
        PlayerInventory inventory2 = new PlayerInventory(playerCards,
                20, destinations);

        playerGameState1 = new PlayerGameState(map, orderOfPlayerTurns, currentPlayer, connectionsStatus, inventory1);

        playerGameState2 = new PlayerGameState(map, orderOfPlayerTurns, currentPlayer, connectionsStatus2, inventory2);

        destinationsToPickFrom = utils.createNewDestinationsToPickFrom();
    }

    @Test
    public void testPickConnectionHold10() {
        HashSet<City> pair = new HashSet<>();
        pair.add(TestUtils.boston);
        pair.add(TestUtils.miami);
        assertEquals(new Connection(ConnectionColor.white, 3, pair), hold10.pickConnection(playerGameState1));
    }

    @Test
    public void testPickConnectionHold10NoConnectionsToPickFrom() {
        expectedEx.expect(IndexOutOfBoundsException.class);
        hold10.pickConnection(playerGameState2);
    }

    @Test
    public void testPickConnectionBuyNow() {
        HashSet<City> pair = new HashSet<>();
        pair.add(TestUtils.boston);
        pair.add(TestUtils.miami);
        assertEquals(new Connection(ConnectionColor.white, 3, pair), buyNow.pickConnection(playerGameState1));
    }

    @Test
    public void testPickConnectionBuyNowNoConnectionsToPickFrom() {
        expectedEx.expect(IndexOutOfBoundsException.class);
        buyNow.pickConnection(playerGameState2);
    }

    @Test
    public void testRequestToAcquireAConnectionHold10CanAcquire() {
        assertTrue(hold10.requestToAcquireAConnection(playerGameState1));
    }

    @Test
    public void testRequestToAcquireAConnectionHold10LessThan10Cards() {
        Map<ConnectionColor, Integer> cards = new HashMap<>();
        for (ConnectionColor color : ConnectionColor.values()) {
            if (color.equals(ConnectionColor.white)) {
                cards.put(color, 3);
            }
            else {
                cards.put(color, 2);
            }
        }

        PlayerInventory inventory = new PlayerInventory( cards,
                playerGameState1.getRailsInHand(), playerGameState1.getDestinations());

        PlayerGameState gameState = new PlayerGameState(playerGameState1.getMap(), orderOfPlayerTurns, currentPlayer,
                playerGameState1.getConnectionsStatus(), inventory);

        assertFalse(hold10.requestToAcquireAConnection(gameState));
    }

    @Test
    public void testRequestToAcquireAConnectionHold10NoAvailableConnections() {
        assertFalse(hold10.requestToAcquireAConnection(playerGameState2));
    }

    @Test
    public void testRequestToAcquireAConnectionBuyNowNormal() {
        // Request a connection with a playergamestate1
        assertTrue(buyNow.requestToAcquireAConnection(playerGameState1));
    }

    @Test
    public void testRequestToAcquireAConnectionBuyNowLessThan10Cards() {
        Map<ConnectionColor, Integer> cards = new HashMap<>();
        for (ConnectionColor color : ConnectionColor.values()) {
            if (color.equals(ConnectionColor.white)) {
                cards.put(color, 3);
            }
            else {
                cards.put(color, 2);
            }
        }
        PlayerInventory inventory = new PlayerInventory( cards,
                playerGameState1.getRailsInHand(), playerGameState1.getDestinations());

        PlayerGameState gameState = new PlayerGameState(playerGameState1.getMap(), orderOfPlayerTurns, currentPlayer,
                playerGameState1.getConnectionsStatus(), inventory);

        // Request a connection with a playergamestate1 with less than 10 cards
        assertTrue(buyNow.requestToAcquireAConnection(gameState));
    }

    @Test
    public void testRequestToAcquireAConnectionBuyNowNoAvailableConnections() {
        assertFalse(buyNow.requestToAcquireAConnection(playerGameState2));
    }

    @Test
    public void testPickDestinationsHold10() {
        Set<Destination> destinations = new HashSet<>();
        destinations.add(new Destination(TestUtils.boston, TestUtils.newYork));
        destinations.add(new Destination(TestUtils.miami, TestUtils.boston));
        assertEquals(destinations, hold10.pickDestinations(destinationsToPickFrom));
    }

    @Test
    public void testPickDestinationsHold10NewCity() {
        Set<Destination> destsToPickFrom = new HashSet<>(destinationsToPickFrom);
        destsToPickFrom.add(new Destination(new City("DC", 0.2, 0.4), TestUtils.boston));
        destsToPickFrom.remove(new Destination(TestUtils.newYork, TestUtils.houston));

        Set<Destination> destinations = new HashSet<>();
        destinations.add(new Destination(new City("DC", 0.2, 0.4), TestUtils.boston));
        destinations.add(new Destination(TestUtils.boston, TestUtils.miami));
        assertEquals(destinations, hold10.pickDestinations(destsToPickFrom));
    }

    @Test
    public void testPickDestinationsBuyNow() {
        Set<Destination> destinations = new HashSet<>();
        destinations.add(new Destination(TestUtils.newYork, TestUtils.houston));
        destinations.add(new Destination(TestUtils.miami, TestUtils.losAngeles));
        assertEquals(destinations, buyNow.pickDestinations(destinationsToPickFrom));
    }

    @Test
    public void testPickDestinationsBuyNowNewCity() {
        Set<Destination> destsToPickFrom = new HashSet<>(destinationsToPickFrom);
        destsToPickFrom.add(new Destination(new City("Phoenix", 0.2, 0.4), TestUtils.newYork));
        destsToPickFrom.remove(new Destination(TestUtils.newYork, TestUtils.boston));

        Set<Destination> destinations = new HashSet<>();
        destinations.add(new Destination(TestUtils.losAngeles, TestUtils.miami));
        destinations.add(new Destination(new City("Phoenix", 0.2, 0.4), TestUtils.newYork));
        assertEquals(destinations, buyNow.pickDestinations(destsToPickFrom));
    }
}
