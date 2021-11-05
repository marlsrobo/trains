package Other;

import java.util.Objects;
import java.util.regex.Pattern;

/***
 * A data representation for a City in the game (a node in the Map)
 */
public class City implements Comparable<City> {

    private final String name;
    private final double x, y;

    /***
     * Constructor for a City
     * @param name the name of the city
     * @param x the percentage ratio of the x coordinate of this City on the Map (0-1 inclusive)
     * @param y the percentage ratio of the y coordinate of this City on the Map (0-1 inclusive)
     */
    public City(String name, double x, double y) {
        if (x < 0 || x > 1) {
            throw new IllegalArgumentException("The X coordinate percentage for a city must " +
                    "be within the range of 0 and 1 (inclusive)");
        }
        if (y < 0 || y > 1) {
            throw new IllegalArgumentException("The Y coordinate percentage for a city must " +
                    "be within the range of 0 and 1 (inclusive)");
        }
        if (name.length() > 25) {
            throw new IllegalArgumentException("A city's name cannot be more than 25 ASCII " +
                    "characters long");
        }
        if (!Pattern.matches("[a-zA-Z0-9\\ \\.\\,]+", name)) {
            throw new IllegalArgumentException("A city's name must contain only letters of " +
                    "the English alphabet (uppercase or lowercase), digits, " +
                    "spaces, dots, or commas");
        }
        Objects.requireNonNull(name);
        this.name = name;
        this.x = x;
        this.y = y;
    }

    /***
     * Returns the name of this City
     * @return a String of the name of this City
     */
    public String getName() {
        return this.name;
    }


    /**
     * Returns the relative X coordinate of this City
     * @return int x coord
     */
    public double getX() {
        return x;
    }

    /***
     * Get the absolute positioning of this city given a width of a window.
     * @param width the width of a window.
     * @return the absolute x coordinate of this city.
     */
    public double getAbsoluteX(int width) {
        return x * width;
    }

    /**
     * Returns the relative Y coordinate of this City
     * @return int y coord
     */
    public double getY() {
        return y;
    }

    /***
     * Get the absolute positioning of this city given a height of a window.
     * @param height the height of a window.
     * @return the absolute y coordinate of this city.
     */
    public double getAbsoluteY(int height) {
        return y * height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return (Double.compare(city.x, x) == 0 && Double.compare(city.y, y) == 0)
                || name.equals(city.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(City city) {
        return this.name.compareTo(city.name);
    }
}
