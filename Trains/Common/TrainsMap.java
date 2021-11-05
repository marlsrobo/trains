package Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import Other.City;
import Other.Connection;
import Other.Destination;

/***
 * Represents a map for our Trains game.
 */
public class TrainsMap {
    private int width = 700;
    private int height = 700;
    private final Map<String, City> cities;
    private final Set<Connection> connections;

    /***
     * Constructor for a TrainsMap given a set of Cities and Connections.
     * @param cities a Set<City> (cities) for this TrainsMap
     * @param connections a Set<Connection> for this TrainsMap
     * @param width the width of this TrainsMap
     * @param height the height of this TrainsMap
     * @throws IllegalArgumentException if the width, height are out of range.
     *                                  if the cities in the given connections are not in the set of cities given
     */
    public TrainsMap(Set<City> cities, Set<Connection> connections, int width, int height) {
        if (width < 10 || width > 800) {
            throw new IllegalArgumentException("Width should be between 10 and 800 (inclusive)");
        }
        if (height < 10 || height > 800)  {
            throw new IllegalArgumentException("Height should be between 10 and 800 (inclusive)");
        }
        this.width = width;
        this.height = height;
        Objects.requireNonNull(cities);
        Objects.requireNonNull(connections);
        this.cities = this.setUpCities(cities);
        if (!this.areConnectionsCitiesInMapCities(connections)) {
            throw new IllegalArgumentException("Connections' cities must exist in the cities set");
        }
        this.connections = new HashSet<>(connections);
    }

    /***
     * Constructor for a TrainsMap given a set of cities and connections.
     * @param cities a Set<City> (cities) for this TrainsMap
     * @param connections a Set<Connection> for this TrainsMap
     * @throws IllegalArgumentException if the cities in the given connections are not in the set of cities given
     */
    public TrainsMap(Set<City> cities, Set<Connection> connections) throws IllegalArgumentException {
        Objects.requireNonNull(cities);
        Objects.requireNonNull(connections);
        this.cities = this.setUpCities(cities);
        if (!this.areConnectionsCitiesInMapCities(connections)) {
            throw new IllegalArgumentException("Connections' cities must exist in the cities set");
        }
        this.connections = new HashSet<>(connections);;
    }

    /**
     * Converts a Set of City into a Map of String to City where the String is the city's name
     * @param cities the Set of City
     * @return a Map<String, City> representing cities in a TrainsMap
     */
    private Map<String, City> setUpCities(Set<City> cities) {
        Map<String, City> cityMap = new HashMap<>();
        for (City city : cities) {
            cityMap.put(city.getName(), city);
        }
        return cityMap;
    }

    /**
     * Determines if all the given connections' cities are already in the cities for this TrainsMap
     * @param connections the Set of Connections to check for.
     * @return true if the given Connections' cities are in this TrainsMap's cities, false otherwise
     */
    private boolean areConnectionsCitiesInMapCities(Set<Connection> connections) {
        for (Connection c : connections) {
            if (!this.areConnectionCitiesInMapCities(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if all the given connection's cities are already in the cities for this TrainsMap.
     * @param connection the given Connection to check for.
     * @return true if the given Connection's cities are in the TrainsMap's cities, false otherwise
     */
    private boolean areConnectionCitiesInMapCities(Connection connection) {
        for (City c : connection.getCities()) {
            if (!cities.containsValue(c)) {
                return false;
            }
        }
        return true;
    }

    /***
     * Getter for the width in this TrainsMap
     * @return int representing the width
     */
    public int getWidth() {
        return this.width;
    }

    /***
     * Getter for height in this TrainsMap
     * @return integer representing the height
     */
    public int getHeight() {
        return this.height;
    }

    /***
     * Gets a copy of the Set of all the cities on this TrainsMap
     * @return Set of City representing the cities
     */
    public Set<City> getCities() {
        return new HashSet<>(this.cities.values());
    }

    /***
     * Gets a copy of the Set of all the connections in a TrainsMap
     * @return Set of Connection representing the connections
     */
    public Set<Connection> getConnections() {
        return new HashSet<>(this.connections);
    }

    /**
     * Gets all the names of the cities in this TrainsMap
     * @return Set of String representing the cities' names
     */
    public Set<String> getCityNames() {
        return new HashSet<>(this.cities.keySet());
    }

    /**
     * Returns the City in this map whose name matches that of the given String
     * @param cityName the name of the City that we want to retrieve
     * @return the City whose name we are searching for
     */
    public City getCityFromName(String cityName) {
        return this.cities.get(cityName);
    }

    /**
     * Finds all the feasible destinations within this TrainsMap. A feasible destination in this context is a pair of
     * cities that is reachable directly or indirectly, by connections in this TrainsMap.
     * @return Set of Destination
     */
    public Set<Destination> getFeasibleDestinations() {
        // the pairs of Cities that we've iterated through
        Set<Destination> destinationsSeen = new HashSet<>();
        // the pairs of Cities that have routes between them
        Set<Destination> destinations = new HashSet<>();

        for (City c1 : this.cities.values()) {
            for (City c2 : this.cities.values()) {
                // if the cities are the same, skip (not a possible destination)
                if (!c1.equals(c2)) {
                    Destination destinationSeen = new Destination(c1, c2);
                    // if we've already iterated through this pair of Cities, skip
                    if (destinationsSeen.contains(destinationSeen)) {
                        continue;
                    }
                    // note that we've seen this pair of cities
                    destinationsSeen.add(destinationSeen);
                    // add this pair of cities to destinations if there's a feasible route
                    if (routeExistsBetweenCities(c1.getName(), c2.getName())) {
                        destinations.add(destinationSeen);
                    }
                }
            }
        }
        return destinations;
    }

    /**
     * Checks if there is a route that exists between 2 given cities
     * @param city1Name String representing the name of the first city in the route
     * @param city2Name String representing the name of the other city in the route
     * @return true if there is a route between the cities, false otherwise
     */
    public boolean routeExistsBetweenCities(String city1Name, String city2Name) {
        City city1 = null;
        City city2 = null;
        for (City city : cities.values()) {
            if (city.getName().equals(city1Name)) {
                city1 = city;
            }
            if (city.getName().equals(city2Name)) {
                city2 = city;
            }
        }
        return this.routeExistsBetweenCitiesAcc(city1, city2, new HashSet<>());
    }

    /**
     * Accumulator method for routeExistsBetweenCities.
     * @param city1 City representing the first city in the route
     * @param city2 City representing the other city in the route
     * @param acc Set of Connection representing an accumulator for connections we've already checked
     * @return true if there is a route between the cities, false if not
     */
    private boolean routeExistsBetweenCitiesAcc(City city1, City city2, Set<Connection> acc) {
        for (Connection connection : this.connections) {
            // if we've already checked this connection, skip
            if (!acc.contains(connection)) {
                List<City> endpoints = new ArrayList<>(connection.getCities());
                // if the 2 cities have a direct connection, return true
                if (endpoints.contains(city1)) {
                    if (endpoints.contains(city2)) {
                        return true;
                    }
                    // otherwise, recur on the city that city1 is connected to in order to check if
                    // there is a route from city1 to city2 through the other city in this connection
                    else {
                        acc.add(connection);
                        if (endpoints.get(0).equals(city1)) {
                            if (this.routeExistsBetweenCitiesAcc(endpoints.get(1), city2, acc)) {
                                return true;
                            }
                        } else {
                            if (this.routeExistsBetweenCitiesAcc(endpoints.get(0), city2, acc)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
