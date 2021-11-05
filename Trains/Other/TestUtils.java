package Other;

import Common.TrainsMap;

import java.util.*;

/**
 * A utility class used for testing purposes only.
 */
public class TestUtils {
    public static final City boston = new City("Boston", 0.9, 0.1);
    public static final City newYork = new City("New York City", 0.8, 0.2);
    public static final City losAngeles = new City("Los Angeles", 0.1, 0.7);
    public static final City miami = new City("Miami", 0.9, 0.9);
    public static final City houston = new City("Houston", 0.5, 0.85);

    public static final Set<City> pair1 = setUpPair(boston, newYork);
    public static final Set<City> pair2 = setUpPair(losAngeles, newYork);
    public static final Set<City> pair3 = setUpPair(boston, miami);
    public static final Set<City> pair4 = setUpPair(miami, losAngeles);

    public static final Connection bostonNYCRed3 =  new Connection(ConnectionColor.red, 3, pair1);
    public static final Connection losAngelesNYCBlue5 = new Connection(ConnectionColor.blue, 5, pair2);
    public static final Connection bostonNYCGreen4 = new Connection(ConnectionColor.green, 4, pair1);
    public static final Connection bostonMiamiWhite3 = new Connection(ConnectionColor.white, 3, pair3);
    public static final Connection miamiLAWhite3 = new Connection(ConnectionColor.white, 3, pair4);

    public static final Set<City> cities = new HashSet<>();
    public static final Set<Connection> connections = new HashSet<>();
    public final TrainsMap map = createMap();
    public final List<Integer> playerTurns = createOrderPlayerTurns();

    private void setUpCities() {
        cities.add(boston);
        cities.add(newYork);
        cities.add(losAngeles);
        cities.add(miami);
    }

    private static Set<City> setUpPair(City c1, City c2) {
        Set<City> pair = new HashSet<>();
        pair.add(c1);
        pair.add(c2);
        return pair;
    }

    private void setUpConnections() {
        connections.add(new Connection(ConnectionColor.red, 3, pair1));
        connections.add(new Connection(ConnectionColor.blue, 5, pair2));
        connections.add(new Connection(ConnectionColor.green, 4, pair1));
        connections.add(new Connection(ConnectionColor.white, 3, pair3));
        connections.add(new Connection(ConnectionColor.white, 3, pair4));
    }

    public TrainsMap createMap() {
        setUpCities();
        setUpConnections();
        return new TrainsMap(cities, connections);
    }

    public List<Integer> createOrderPlayerTurns() {
        List<Integer> players = new ArrayList<>();
        players.add(1);
        players.add(2);
        players.add(3);
        players.add(4);
        return players;
    }

    public Map<ConnectionColor, Integer> createDeck() {
        Map<ConnectionColor, Integer> deck = new HashMap<>();
        deck.put(ConnectionColor.blue, 50);
        deck.put(ConnectionColor.red, 50);
        deck.put(ConnectionColor.green, 50);
        deck.put(ConnectionColor.white, 50);
        return deck;
    }

    public Map<Connection, Integer> createConnectionsStatus(int playerId) {
        Map<Connection, Integer> connectionsStatus = new HashMap<>();
        for (Connection connection : map.getConnections()) {
            connectionsStatus.put(connection, playerId);
        }
        return connectionsStatus;
    }

    public Map<Integer, Integer> createRailsPerPlayer() {
        Map<Integer, Integer> railsPerPlayer = new HashMap<>();
        for (Integer id : playerTurns) {
            railsPerPlayer.put(id, 40);
        }
        return railsPerPlayer;
    }

    public Map<Integer, Map<ConnectionColor, Integer>> createCardsPerPlayer() {
        Map<Integer, Map<ConnectionColor, Integer>> cardsPerPlayer = new HashMap<>();
        Map<ConnectionColor, Integer> hand = new HashMap<>();
        hand.put(ConnectionColor.blue, 5);
        hand.put(ConnectionColor.red, 5);
        hand.put(ConnectionColor.green, 5);
        hand.put(ConnectionColor.white, 5);
        for (Integer id : playerTurns) {
            cardsPerPlayer.put(id, hand);
        }
        return cardsPerPlayer;
    }

    public Set<Destination> createDestination() {
        Set<Destination> destinations = new HashSet<>();
        destinations.add(new Destination(boston, newYork));
        destinations.add(new Destination(newYork, houston));

        return destinations;
    }

    public Map<Integer, Set<Destination>> createDestinationsPerPlayer() {
        Map<Integer, Set<Destination>> destinationsPerPlayer = new HashMap<>();
        for (Integer id : playerTurns) {
            Set<Destination> destinations = new HashSet<>();
            destinations.add(new Destination(boston, newYork));
            destinations.add(new Destination(newYork, houston));
            destinationsPerPlayer.put(id, destinations);
        }

        return destinationsPerPlayer;
    }

    public Set<Destination> createNewDestinationsToPickFrom() {
        Set<Destination> destinations = new HashSet<>();
        destinations.add(new Destination(boston, newYork));
        destinations.add(new Destination(boston, miami));
        destinations.add(new Destination(houston, losAngeles));
        destinations.add(new Destination(newYork, houston));
        destinations.add(new Destination(losAngeles, miami));
        return destinations;
    }
}
