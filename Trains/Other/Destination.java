package Other;

import java.util.Objects;

/**
 * Representation for a Destination in a game of Trains that contains 2 cities that
 * are guaranteed to be connection by a route.
 */
public class Destination implements Comparable<Destination> {

  private final City city1;
  private final City city2;

  /**
   * Constructor for Destination. Orders the cities so that city1 is
   * always before city2 lexicographically.
   * @param city1 the first City in the Destination
   * @param city2 the second City in the Destination
   */
  public Destination(City city1, City city2) {
    if (city1.compareTo(city2) == 0) {
      throw new IllegalArgumentException("Destination cannot have the same cities");
    }
    else if (city1.compareTo(city2) < 0) {
      this.city1 = city1;
      this.city2 = city2;
    }
    else {
      this.city1 = city2;
      this.city2 = city1;
    }
  }

  /**
   * Getter for city1
   * @return the City for city1
   */
  public City getCity1() {
    return city1;
  }

  /**
   * Getter for city2
   * @return the City for city2
   */
  public City getCity2() {
    return city2;
  }

  @Override
  public int hashCode() {
    return Objects.hash(city1, city2);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Destination destination = (Destination) o;
    return destination.city1.equals(this.city1) && destination.city2.equals(this.city2);
  }

  @Override
  public int compareTo(Destination destination) {
     if (this.city1.compareTo(destination.city1) == 0) {
       return this.city2.compareTo(destination.city2);
     }
     else {
       return this.city1.compareTo(destination.city1);
     }
  }

  @Override
  public String toString() {
    return "Destination{" +
            "city1=" + city1 +
            ", city2=" + city2 +
            '}';
  }
}
