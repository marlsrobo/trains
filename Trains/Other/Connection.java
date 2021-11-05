package Other;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/***
 * Represents a Connection between two cities in a map
 */
public class Connection implements Comparable<Connection> {
    private final ConnectionColor color;
    private final int length;
    private final Set<City> cities;

    /***
     * Constructor for a Connection
     * @param color the color of the Connection (red, white, blue or green)
     * @param length the length of the Connection (3, 4 or 5)
     * @param cities the 2 cities that are connected by this Connection
     */
    public Connection(ConnectionColor color, int length, Set<City> cities) {
        if (cities.size() != 2) {
            throw new IllegalArgumentException("Connection must have 2 cities");
        }
        if (length < 3 || length > 5) {
            throw new IllegalArgumentException("Length must be 3, 4, or 5");
        }
        Objects.requireNonNull(color);
        Objects.requireNonNull(cities);
        this.color = color;
        this.length = length;
        this.cities = new HashSet<>(cities);
    }

    /**
     * Constructor for a Connection. Takes in 2 cities instead of a set of city.
     * @param color the color of the Connection (red, white, blue or green)
     * @param length the length of the Connection (3, 4 or 5)
     * @param city1 the first City in the Connection
     * @param city2 the second City in the Connection
     */
    public Connection(ConnectionColor color, int length, City city1, City city2) {
        if (length < 3 || length > 5) {
            throw new IllegalArgumentException("Length must be 3, 4, or 5");
        }
        if (city1.equals(city2)) {
            throw new IllegalArgumentException("Connection can't have duplicate cities");
        }
        Objects.requireNonNull(color);
        Objects.requireNonNull(city1);
        Objects.requireNonNull(city2);
        this.color = color;
        this.length = length;
        this.cities = new HashSet<>();
        this.cities.add(city1);
        this.cities.add(city2);
    }

    /***
     * Returns a copy of the set of the connected cities
     * @return Set of City representing cities
     */
    public Set<City> getCities() {
        return new HashSet<>(this.cities);
    }

    /***

     * Returns true if this Connection has the same cities as the given Connection
     * @param c the given Connection
     * @return true if cities are equal
     */
    public boolean hasSameCities(Connection c) {
        return this.getCities().equals(c.getCities());
    }

    /***
     * Returns the length of the connection
     * @return int representing 'length' of the Connection
     */
    public int getLength() {
        return this.length;
    }

    /***
     * Returns the Color of the ConnectionColor of this connection
     * @return Color representing the 'color'
     */
    public Color getColor() {
        return this.color.getColor();
    }

    /***
     * Returns the ConnectionColor of this connection
     * @return ConnectionColor representing the 'color'
     */
    public ConnectionColor getConnectionColor() {
        return this.color;
    }

    // A Connection is equal to another Connection if they have the same color and cities
    // We are not checking for length because when adding Connections to the Map, we want to
    // ensure that there are no Connections between 2 Cities with the same color, but there
    // can be more than 1 Connection between 2 Cities with the same length
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection that = (Connection) o;
        return color == that.color && cities.equals(that.cities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, cities);
    }

    @Override
    public String toString() {
        return "Connection{" +
                "color=" + color +
                ", length=" + length +
                ", cities=" + cities +
                '}';
    }

    @Override
    public int compareTo(Connection connection) {
        List<City> thisCities = new ArrayList<>(this.cities);
        List<City> otherCities = new ArrayList<>(connection.cities);
        Collections.sort(thisCities);
        Collections.sort(otherCities);
        if (thisCities.get(0).compareTo(otherCities.get(0)) != 0) {
            return thisCities.get(0).compareTo(otherCities.get(0));
        }
        else if (thisCities.get(1).compareTo(otherCities.get(1)) != 0) {
            return thisCities.get(1).compareTo(otherCities.get(1));
        }
        else if (this.length != connection.length) {
            return this.length - connection.length;
        }
        else {
            return this.color.toString().compareTo(connection.color.toString());
        }
    }
}
