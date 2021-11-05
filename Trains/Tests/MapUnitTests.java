import Other.Destination;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import Common.TrainsMap;
import Other.City;
import Other.Connection;
import Other.ConnectionColor;
import org.junit.rules.ExpectedException;

import static junit.framework.TestCase.assertEquals;

/**
 * Unit tests for TrainsMap.
 */
public class MapUnitTests {
  @Rule
  public final ExpectedException expectedEx = ExpectedException.none();

  private final City boston = new City("Boston", 0.9, 0.1);
  private final City newYork = new City("New York City", 0.8, 0.2);
  private final City losAngeles = new City("Los Angeles", 0.1, 0.7);
  private final City miami = new City("Miami", 0.9, 0.9);
  private final City houston = new City("Houston", 0.5, 0.85);

  private final Set<City> pair1 = new HashSet<>();
  private final Set<City> pair2 = new HashSet<>();
  private final Set<City> pair3 = new HashSet<>();
  private final Set<City> pair4 = new HashSet<>();
  private final Set<City> pair5 = new HashSet<>();
  private final Set<City> pair6 = new HashSet<>();
  private final Set<City> pair7 = new HashSet<>();
  private final Set<City> pair8 = new HashSet<>();

  private final Set<City> cities = new HashSet<>();
  private final Set<City> notFullyConnectedCities = new HashSet<>();
  private final Set<Connection> connections = new HashSet<>();
  private final Set<Connection> notFullyConnectedConnections = new HashSet<>();

  @Before
  public void setUpCities() {
    cities.add(boston);
    cities.add(newYork);
    cities.add(losAngeles);
    cities.add(miami);
  }

  @Before
  public void setUpCitiesNotFullyConnected() {
    notFullyConnectedCities.add(boston);
    notFullyConnectedCities.add(newYork);
    notFullyConnectedCities.add(losAngeles);
    notFullyConnectedCities.add(miami);
    notFullyConnectedCities.add(houston);
  }

  /***
   * Helper for setting up connections.
   */
  private void setUpPairs() {
    pair1.add(boston);
    pair1.add(newYork);

    pair2.add(losAngeles);
    pair2.add(newYork);

    pair3.add(boston);
    pair3.add(miami);

    pair4.add(miami);
    pair4.add(losAngeles);

    pair5.add(boston);
    pair5.add(losAngeles);

    pair6.add(miami);
    pair6.add(newYork);

    pair7.add(boston);
    pair7.add(houston);

    pair8.add(newYork);
    pair8.add(houston);
  }

  @Before
  public void setUpConnections() {
    setUpPairs();
    connections.add(new Connection(ConnectionColor.red, 3, pair1));
    connections.add(new Connection(ConnectionColor.blue, 5, pair2));
    connections.add(new Connection(ConnectionColor.green, 4, pair1));
    connections.add(new Connection(ConnectionColor.white, 3, pair3));
    connections.add(new Connection(ConnectionColor.white, 3, pair4));
  }

  @Before
  public void setUpNotFullyConnectedConnections() {
    setUpPairs();
    notFullyConnectedConnections.add(new Connection(ConnectionColor.blue, 5, pair1));
    notFullyConnectedConnections.add(new Connection(ConnectionColor.white, 3, pair4));
    notFullyConnectedConnections.add(new Connection(ConnectionColor.red, 4, pair7));
  }

  @Test
  public void testIllegalCitiesInConnections() {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("Connections' cities must exist in the cities set");

    Set<Connection> badConnections = new HashSet<>(connections);
    Set<City> pairBad = new HashSet<>();
    pairBad.add(new City("City not in map cities", 0.347, 0.467));
    pairBad.add(boston);
    badConnections.add(new Connection(ConnectionColor.red, 3, pairBad));

    // Add bad connections to a new TrainsMap
    TrainsMap map = new TrainsMap(cities, badConnections);
  }

  @Test
  public void testIllegalMapWidth() {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("Width should be between 10 and 800 (inclusive)");

    // Create an illegal map with wrong width
    TrainsMap map = new TrainsMap(cities, connections, 801, 10);
  }

  @Test
  public void testIllegalMapHeight() {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("Height should be between 10 and 800 (inclusive)");

    // Create an illegal map with wrong height
    TrainsMap map = new TrainsMap(cities, connections, 800, 9);
  }

  @Test
  public void testGetCities() {
    TrainsMap map = new TrainsMap(cities, connections, 100, 200);

    assertEquals(4, map.getCities().size());
    assertEquals(cities, map.getCities());
  }

  @Test
  public void testGetConnections() {
    TrainsMap map = new TrainsMap(cities, connections, 400, 200);

    Set<Connection> newConnections = new HashSet<>(connections);
    newConnections.add(new Connection(ConnectionColor.red, 3, pair1));

    assertEquals(5, map.getConnections().size());
    assertEquals(newConnections, map.getConnections());
  }

  @Test
  public void testGetCityNames() {
    TrainsMap map = new TrainsMap(cities, connections, 100, 200);

    // Added in different order than map's cities.
    Set<String> cityNames = new HashSet<>();
    cityNames.add("Miami");
    cityNames.add("New York City");
    cityNames.add("Boston");
    cityNames.add("Los Angeles");

    assertEquals(4, map.getCityNames().size());
    assertEquals(cityNames, map.getCityNames());
  }

  @Test
  public void testGetFeasibleDestinations() {
    TrainsMap map = new TrainsMap(cities, connections);

    Set<Destination> destinations = new HashSet<>();
    Destination d1 = new Destination(boston, newYork);
    destinations.add(d1);
    Destination d2 = new Destination(losAngeles, newYork);
    destinations.add(d2);
    Destination d3 = new Destination(boston, miami);
    destinations.add(d3);
    Destination d4 = new Destination(miami, losAngeles);
    destinations.add(d4);
    Destination d5 = new Destination(boston, losAngeles);
    destinations.add(d5);
    Destination d6 = new Destination(miami, newYork);
    destinations.add(d6);

    assertEquals(destinations, map.getFeasibleDestinations());
  }

  @Test
  public void testGetFeasibleDestinationsNotFullyConnected() {
    TrainsMap notFullyConnectedMap = new TrainsMap(notFullyConnectedCities, notFullyConnectedConnections);

    Set<Destination> destinations = new HashSet<>();
    Destination d1 = new Destination(boston, newYork);
    destinations.add(d1);
    Destination d4 = new Destination(miami, losAngeles);
    destinations.add(d4);
    Destination d7 = new Destination(boston, houston);
    destinations.add(d7);
    Destination d8 = new Destination(newYork, houston);
    destinations.add(d8);

    assertEquals(destinations, notFullyConnectedMap.getFeasibleDestinations());
  }

  @Test
  public void testGetWidth() {
    TrainsMap map = new TrainsMap(cities, connections, 300, 500);
    assertEquals(300, map.getWidth());
  }

  @Test
  public void testGetHeight() {
    TrainsMap map = new TrainsMap(cities, connections, 150, 200);
    assertEquals(200, map.getHeight());
  }
}
