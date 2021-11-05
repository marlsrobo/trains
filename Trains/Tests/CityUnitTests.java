import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

import Other.City;

/**
 * Unit tests for City.
 */
public class CityUnitTests {

  City newYork = new City("NYC", 0.1, 0.2);

  @Test(expected = IllegalArgumentException.class)
  public void xCoordTooLow() {
    City city = new City("b", -1, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void xCoordTooHigh() {
    City city = new City("Boston", 1.2, 0.5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void yCoordTooLow() {
    City city = new City("Boston", 0.3, -0.1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void yCoordTooHigh() {
    City city = new City("Boston", 0.45, 1.01);
  }

  @Test
  public void testGetName() {
    assertEquals("NYC", newYork.getName());
  }

  @Test
  public void testGetX() {
    assertEquals(0.1, newYork.getX());
  }

  @Test
  public void testGetY() {
    assertEquals(0.2, newYork.getY());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCityNameBad() {
    City newYorkBadName = new City("New York!!", 0.80, 0.30);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCityNameTooLong() {
    City newYorkBadName = new City("New York fhdjskalbgdfsabhj",
            0.80, 0.30);
  }

  @Test
  public void testCityNameLongWithAllGoodCharacters() {
    City city = new City("hello, I am a city.......", 0.80, 0.30);
  }

  @Test
  public void testCitiesInLexOrderByName() {
    City boston = new City("boston", 0.10, 0.12);
    City Boston = new City("Boston", 0.10, 0.12);
    City nyc = new City("nyc", 0.10, 0.12);
    City NYC = new City("NYC", 0.10, 0.12);

    List<City> expected = new ArrayList<>();
    expected.add(Boston);
    expected.add(boston);
    expected.add(NYC);
    expected.add(nyc);

    List<City> unsorted = new ArrayList<>();
    unsorted.add(nyc);
    unsorted.add(Boston);
    unsorted.add(boston);
    unsorted.add(NYC);

    Collections.sort(unsorted);
    List<City> actual = new ArrayList<>(unsorted);

    assertEquals(expected, actual);
  }

}
