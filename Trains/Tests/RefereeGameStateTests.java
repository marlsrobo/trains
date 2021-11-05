import Admin.RefereeGameState;
import Common.PlayerGameState;
import Other.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Common.TrainsMap;
import org.junit.rules.ExpectedException;

import static junit.framework.TestCase.*;


/**
 * Unit tests for RefereeGameState.
 */
public class RefereeGameStateTests {
    @Rule
    public final ExpectedException expectedEx = ExpectedException.none();

    TestUtils utils = new TestUtils();
    private TrainsMap map;
    Map<ConnectionColor, Integer> deck;
    private List<Integer> orderOfPlayerTurns;
    private int currentPlayer;
    Map<Connection, Integer> connectionsStatus;
    Map<Integer, Integer> railsPerPlayer;
    Map<Integer, Map<ConnectionColor, Integer>> cardsPerPlayer;
    Map<Integer, Set<Destination>> destinationsPerPlayer;


    RefereeGameState refereeGameState1;
    RefereeGameState refereeGameState2;

    PlayerGameState playerGameState1;

    @Before
    public void setup() {
        map = utils.createMap();
        orderOfPlayerTurns = utils.createOrderPlayerTurns();
        currentPlayer = orderOfPlayerTurns.get(0);
        connectionsStatus = utils.createConnectionsStatus(-1);
        railsPerPlayer = utils.createRailsPerPlayer();
        cardsPerPlayer = utils.createCardsPerPlayer();
        destinationsPerPlayer = utils.createDestinationsPerPlayer();

        refereeGameState1 = createRefereeGameState(connectionsStatus);
        refereeGameState2 = new RefereeGameState(map, orderOfPlayerTurns, currentPlayer,
                connectionsStatus, railsPerPlayer, cardsPerPlayer, destinationsPerPlayer);

        PlayerInventory inventory = new PlayerInventory(cardsPerPlayer.get(currentPlayer), railsPerPlayer.get(currentPlayer),
                destinationsPerPlayer.get(currentPlayer));

        playerGameState1 = new PlayerGameState(map, orderOfPlayerTurns, currentPlayer, connectionsStatus,
                inventory);
    }

    /**
     * Creates a referee game state with the given connection status
     * @param connStatus a Map of Connection to Integer
     * @return a new RefereeGameState
     */
    private RefereeGameState createRefereeGameState(Map<Connection, Integer> connStatus) {
        return new RefereeGameState(map, orderOfPlayerTurns, currentPlayer,
                connStatus, railsPerPlayer, cardsPerPlayer, destinationsPerPlayer);
    }


    @Test
    public void testCreateBadRefereeGameStateDupPlayers() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Order of player turns contains duplicate player ids");

        List<Integer> orderPlayers = new ArrayList<>();
        orderPlayers.add(1);
        orderPlayers.add(2);
        orderPlayers.add(1);

        RefereeGameState badState = new RefereeGameState(map, orderPlayers, currentPlayer,
                connectionsStatus, railsPerPlayer, cardsPerPlayer, destinationsPerPlayer);
    }

    @Test
    public void testCreateBadRefereeGameStateConnectionStatusAsID() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Order of player turns cannot contain -1");

        List<Integer> orderPlayers = new ArrayList<>();
        orderPlayers.add(-1);
        orderPlayers.add(2);
        orderPlayers.add(1);

        RefereeGameState badState = new RefereeGameState(map, orderPlayers, currentPlayer,
                connectionsStatus, railsPerPlayer, cardsPerPlayer, destinationsPerPlayer);
    }

    @Test
    public void testCreateBadRefereeGameStateCurrentPlayerIDConnectionStatus() {
        expectedEx.expectMessage("Current player id cannot be -1");
        RefereeGameState badState = new RefereeGameState(map, orderOfPlayerTurns, -1,
                connectionsStatus, railsPerPlayer, cardsPerPlayer, destinationsPerPlayer);
    }

    @Test
    public void testCreateBadRefereeGameStatePlayerIdNotInTurnsOrder() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Current player must exist in the order of player turns");
        RefereeGameState badState = new RefereeGameState(map, orderOfPlayerTurns, 432,
                connectionsStatus, railsPerPlayer, cardsPerPlayer, destinationsPerPlayer);
    }


    @Test
    public void testCreateBadRefereeGameStateBadConnectionStatusContainsConnectionsNotInMap() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Connections in the connection statuses must be the " +
                "same connections as the map connections");

        Map<Connection, Integer> status = new HashMap<>(connectionsStatus);
        status.put(new Connection(ConnectionColor.blue, 3, utils.pair4), -1);

        RefereeGameState badState = new RefereeGameState(map, orderOfPlayerTurns, currentPlayer,
                status, railsPerPlayer, cardsPerPlayer, destinationsPerPlayer);
    }

    @Test
    public void testCreateBadRefereeGameStateBadConnectionStatusDoesntContainConnectionFromMap() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Connections in the connection statuses must be the " +
                "same connections as the map connections");

        Map<Connection, Integer> status = new HashMap<>(connectionsStatus);
        status.remove(new Connection(ConnectionColor.red, 3, utils.pair1));

        RefereeGameState badState = new RefereeGameState(map, orderOfPlayerTurns,
                currentPlayer, status, railsPerPlayer, cardsPerPlayer, destinationsPerPlayer);
    }

    @Test
    public void testCreateBadRefereeGameStateBadPlayerInConnectionStatus() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Connection status must be the id of a player in this game or");

        Map<Connection, Integer> status = new HashMap<>(connectionsStatus);
        status.put(new Connection(ConnectionColor.blue, 5, utils.pair2), 10);

        RefereeGameState badState = new RefereeGameState(map, orderOfPlayerTurns, currentPlayer,
                status, railsPerPlayer, cardsPerPlayer, destinationsPerPlayer);
    }

    @Test
    public void testCreateBadRefereeGameStateRailsPerPlayerNegativeRails() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Player cannot have a negative number of rails");

        this.railsPerPlayer.put(3, -2);

        RefereeGameState badState = new RefereeGameState(map, orderOfPlayerTurns, currentPlayer,
                connectionsStatus, railsPerPlayer, cardsPerPlayer, destinationsPerPlayer);
    }

    @Test
    public void testCreateBadRefereeGameStateRailsPerPlayerIDDoesntExistInTurns() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Player ids must be the same as the order of " +
                "player turns ids");

        this.railsPerPlayer.put(-20, 4);

        RefereeGameState badState = new RefereeGameState(map, orderOfPlayerTurns, currentPlayer,
                connectionsStatus, railsPerPlayer, cardsPerPlayer, destinationsPerPlayer);
    }

    @Test
    public void testCreateBadRefereeGameStateRailsPerPlayerMissingId() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Player ids must be the same as the order of " +
                "player turns ids");

        this.railsPerPlayer.remove(1);

        RefereeGameState badState = new RefereeGameState(map, orderOfPlayerTurns,
                currentPlayer, connectionsStatus, railsPerPlayer, cardsPerPlayer, destinationsPerPlayer);
    }

    @Test
    public void testCreateBadRefereeGameStateCardsPerPlayerNotAllColors() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Players' card hands must contain all possible " +
                "colors of colored cards");

        Map<ConnectionColor, Integer> cards = new HashMap<>();
        cards.put(ConnectionColor.red, 5);
        cards.put(ConnectionColor.blue, 6);
        this.cardsPerPlayer.put(2, cards);

        RefereeGameState badState = new RefereeGameState(map, orderOfPlayerTurns,
                currentPlayer, connectionsStatus, railsPerPlayer, cardsPerPlayer, destinationsPerPlayer);
    }

    @Test
    public void testCreateBadRefereeGameStateCardsPerPlayerNegativeCards() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Cannot have a negative number of colored cards " +
                "for a player");

        Map<ConnectionColor, Integer> cards = new HashMap<>();
        cards.put(ConnectionColor.red, 5);
        cards.put(ConnectionColor.blue, -6);
        cards.put(ConnectionColor.green, 3);
        cards.put(ConnectionColor.white, 0);
        this.cardsPerPlayer.put(2, cards);

        RefereeGameState badState = new RefereeGameState(map, orderOfPlayerTurns,
                currentPlayer, connectionsStatus, railsPerPlayer, cardsPerPlayer, destinationsPerPlayer);
    }

    @Test
    public void testCreateBadRefereeGameStateCardsPerPlayerIDDoesntExistInTurns() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Player ids must be the same as the order of " +
                "player turns ids");

        Map<ConnectionColor, Integer> cards = new HashMap<>();
        cards.put(ConnectionColor.red, 5);
        cards.put(ConnectionColor.blue, 6);
        cards.put(ConnectionColor.green, 3);
        cards.put(ConnectionColor.white, 0);
        this.cardsPerPlayer.put(27, cards);

        RefereeGameState badState = new RefereeGameState(map, orderOfPlayerTurns,
                currentPlayer, connectionsStatus, railsPerPlayer, cardsPerPlayer, destinationsPerPlayer);
    }

    @Test
    public void testCreateBadRefereeGameStateCardsPerPlayerMissingId() {
        expectedEx.expect(IllegalArgumentException.class);
        expectedEx.expectMessage("Player ids must be the same as the order of " +
                "player turns ids");

        this.cardsPerPlayer.remove(1);

        RefereeGameState badState = new RefereeGameState(map, orderOfPlayerTurns,
                currentPlayer, connectionsStatus, railsPerPlayer, cardsPerPlayer, destinationsPerPlayer);
    }

    @Test
    public void testIsLegalAcquisitionValid() {
        City boston = new City("Boston", 0.9, 0.1);
        City newYork = new City("New York City", 0.8, 0.2);

        Set<City> pair1 = new HashSet<>();
        pair1.add(boston);
        pair1.add(newYork);

        assertTrue(refereeGameState1.isLegalAcquisition(new Connection(ConnectionColor.red,
                3, pair1)));
    }

    @Test
    public void testIsLegalAcquisitionNotEnoughCards() {

        List<Integer> playerTurns = new ArrayList<>();
        playerTurns.add(123);

        Map<ConnectionColor, Integer> cards = new HashMap<>();
        cards.put(ConnectionColor.red, 1);
        cards.put(ConnectionColor.green, 7);
        cards.put(ConnectionColor.white, 5);
        cards.put(ConnectionColor.blue, 5);

        Set<Destination> dests = new HashSet<>();
        dests.add(new Destination(TestUtils.miami, TestUtils.houston));
        dests.add(new Destination(TestUtils.miami, TestUtils.boston));

        Map<Integer, Integer> railsPerPlayer = new HashMap<>();
        railsPerPlayer.put(123, 5);

        Map<Integer, Map<ConnectionColor, Integer>> playersCards = new HashMap<>();
        playersCards.put(123, cards);

        Map<Integer, Set<Destination>> playerDestinations = new HashMap<>();
        playerDestinations.put(123, dests);

        RefereeGameState refGameState = new RefereeGameState(this.map,
                playerTurns, 123, this.connectionsStatus,
                railsPerPlayer, playersCards, playerDestinations);
        City boston = new City("Boston", 0.9, 0.1);
        City newYork = new City("New York City", 0.8, 0.2);

        Set<City> pair1 = new HashSet<>();
        pair1.add(boston);
        pair1.add(newYork);

        assertFalse(refGameState.isLegalAcquisition(new Connection(ConnectionColor.red, 3, pair1)));
    }

    @Test
    public void testIsLegalAcquisitionConnectionAlreadyAcquired() {

        RefereeGameState refGameState = new RefereeGameState(map, orderOfPlayerTurns, 2,
                utils.createConnectionsStatus(3), railsPerPlayer, cardsPerPlayer,
                destinationsPerPlayer);
        City boston = new City("Boston", 0.9, 0.1);
        City newYork = new City("New York City", 0.8, 0.2);

        Set<City> pair1 = new HashSet<>();
        pair1.add(boston);
        pair1.add(newYork);

        assertFalse(refGameState.isLegalAcquisition(new Connection(ConnectionColor.red, 3, pair1)));
    }

    @Test
    public void testGetMap() {
        assertEquals(refereeGameState1.getMap(), this.map);
    }


    @Test
    public void testGetOrderOfPlayerTurns() {
        assertEquals(refereeGameState1.getOrderOfPlayerTurns(), this.orderOfPlayerTurns);
    }

    @Test
    public void testGetCurrentPlayer() {
        assertEquals(refereeGameState1.getCurrentPlayer(), this.currentPlayer);
    }

    @Test
    public void testGetConnectionsStatus() {
        assertEquals(refereeGameState1.getConnectionsStatus(), this.connectionsStatus);
    }

    @Test
    public void testGetRailsPerPlayer() {
        assertEquals(refereeGameState1.getRailsForCurrentPlayer(), this.railsPerPlayer.get(currentPlayer));
    }

    @Test
    public void testGetCardsPerPlayer() {
       assertEquals(playerGameState1.getCardsInHand(),
               refereeGameState1.getCardsForCurrentPlayer());
    }

    @Test
    public void testCreateCurrentPlayerGameState() {
        PlayerInventory inventory = new PlayerInventory(cardsPerPlayer.get(1), railsPerPlayer.get(1),
                destinationsPerPlayer.get(1));

        PlayerGameState playerGameState = new PlayerGameState(map, orderOfPlayerTurns, currentPlayer,
                connectionsStatus, inventory);

        assertEquals(refereeGameState1.createCurrentPlayerGameState(), playerGameState);
    }
}

