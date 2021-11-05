import Common.PlayerGameState;
import Common.TrainsMap;
import Other.*;
import Other.Action.Action;
import Other.Action.ActionEnum;
import Other.Strategies.BuyNowStrategy;
import Other.Strategies.Hold10Strategy;
import Player.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.*;

/**
 * Tests for a Player
 */
public class PlayerTests {
    TestUtils utils = new TestUtils();

    private TrainsMap map;
    private List<Integer> orderOfPlayerTurns;
    private int currentPlayer;
    Map<Connection, Integer> connectionsStatus;

    PlayerInventory inventory1;
    PlayerGameState playerGameState1;

    Player player1;
    Player player2;

    private Set<Destination> destinationsToPickFrom = new HashSet<>();

    Map<ConnectionColor, Integer> playerCards;

    @Before
    public void setup() {
        map = utils.map;
        orderOfPlayerTurns = utils.playerTurns;
        currentPlayer = orderOfPlayerTurns.get(0);
        connectionsStatus = utils.createConnectionsStatus(-1);
        playerCards = new HashMap<>();

        playerCards.put(ConnectionColor.red, 1);
        playerCards.put(ConnectionColor.green, 1);
        playerCards.put(ConnectionColor.white, 1);
        playerCards.put(ConnectionColor.blue, 6);

        Set<Destination> destinations = new HashSet<>();
        destinations.add(new Destination(TestUtils.boston, TestUtils.newYork));
        destinations.add(new Destination(TestUtils.newYork, TestUtils.houston));

        this.inventory1 = new PlayerInventory(playerCards, 40, destinations);

        playerGameState1 = new PlayerGameState(map, orderOfPlayerTurns, currentPlayer, connectionsStatus, inventory1);

        player1 = new Player(15, playerGameState1,"$PWD/Trains/Other/Strategies/BuyNowStrategy.java");
        player2 = new Player(56, playerGameState1,"$PWD/Trains/Other/Strategies/Hold10Strategy.java");

        destinationsToPickFrom = utils.createNewDestinationsToPickFrom();

    }

    @Test
    public void testSetUp() {
        Map<ConnectionColor, Integer> playerCards = new HashMap<>();
        for (ConnectionColor color : ConnectionColor.values()) {
            playerCards.put(color, 2);
        }
        player1.setup(map, 46, playerCards);
        player2.setup(map, 32, playerCards);

        assertEquals(player1.getInventory().getCardsInHand(), new HashMap<>(playerCards));
        assertEquals(player1.getInventory().getRailsInHand(), 46);
        assertEquals(player2.getInventory().getRailsInHand(), 32);
        assertEquals(player1.getInventory().getDestinations(), new HashSet<>());
    }

    @Test
    public void testPick() {
        List<Destination> destinationList = new ArrayList<>();
        destinationList.add(new Destination(TestUtils.boston, TestUtils.newYork));
        destinationList.add(new Destination(TestUtils.boston, TestUtils.miami));
        destinationList.add(new Destination(TestUtils.houston, TestUtils.losAngeles));
        assertEquals(5, destinationsToPickFrom.size());

        player1.pick(destinationsToPickFrom);
        assertEquals(destinationsToPickFrom, new HashSet<>(destinationList));
        assertEquals(3, destinationsToPickFrom.size());

        destinationList.remove(new Destination(TestUtils.boston, TestUtils.newYork));
        destinationList.remove(new Destination(TestUtils.boston, TestUtils.miami));

        player2.pick(destinationsToPickFrom);
        assertEquals(destinationsToPickFrom, new HashSet<>(destinationList));
        assertEquals(1, destinationsToPickFrom.size());

        assertEquals(player1.getInventory().getDestinations().size(), 2);
        assertEquals(player2.getInventory().getDestinations().size(), 2);
        assertNotSame(player1.getInventory().getDestinations(), player2.getInventory().getDestinations());
    }

    @Test
    public void testPlayBuyNow() {
        Action playAction = player1.play(playerGameState1);
        assertEquals(ActionEnum.acquired, playAction.getActionType());

        // Create the acquired that the play method should return.
        HashSet<City> cities = new HashSet<>();
        cities.add(TestUtils.newYork);
        cities.add(TestUtils.losAngeles);
        Connection val = new Connection(ConnectionColor.blue, 5, cities);

        assertEquals(val, playAction.getValue());
    }

    @Test
    public void testPlayHold10() {
        Action playAction = player2.play(playerGameState1);
        assertEquals(playAction.getActionType(), ActionEnum.more_cards);
        assertEquals("more cards", playAction.getValue());
    }

    @Test
    public void testMore() {
        Map<ConnectionColor, Integer> cards = new HashMap<>();
        cards.put(ConnectionColor.numberToColor(0), 3);
        assertEquals(1, player1.getInventory().getCardsInHand().get(ConnectionColor.numberToColor(0)).intValue());
        player1.more(cards); // Adding 3 cards to player1's hand of cards
        assertEquals(4, player1.getInventory().getCardsInHand().get(ConnectionColor.numberToColor(0)).intValue());
    }

    @Test
    public void testMore2() {
        Map<ConnectionColor, Integer> cards = new HashMap<>();
        cards.put(ConnectionColor.numberToColor(0), 3);
        cards.put(ConnectionColor.blue, 2);

        assertEquals(1, player1.getInventory().getCardsInHand().get(ConnectionColor.numberToColor(0)).intValue());
        assertEquals(6, player1.getInventory().getCardsInHand().get(ConnectionColor.blue).intValue());
        assertEquals(1, player1.getInventory().getCardsInHand().get(ConnectionColor.numberToColor(2)).intValue());

        player1.more(cards); // Adding 5 cards to player1's hand of cards
        assertEquals(4, player1.getInventory().getCardsInHand().get(ConnectionColor.numberToColor(0)).intValue());
        assertEquals(8, player1.getInventory().getCardsInHand().get(ConnectionColor.blue).intValue());
        assertEquals(1, player1.getInventory().getCardsInHand().get(ConnectionColor.numberToColor(2)).intValue());

    }

    @Test
    public void testGetPlayerId() {
        assertEquals(player1.getPlayerID(), 15);
        assertEquals(player2.getPlayerID(), 56);
    }

    @Test
    public void testGetPlayerGameState() {
        assertEquals(player1.getGameState(), new PlayerGameState(map, orderOfPlayerTurns, currentPlayer,
                connectionsStatus, inventory1));
    }

    @Test
    public void testGetStrategy() {
        assertTrue(player1.getStrategy() instanceof BuyNowStrategy);
        assertTrue(player2.getStrategy() instanceof Hold10Strategy);
    }

}
