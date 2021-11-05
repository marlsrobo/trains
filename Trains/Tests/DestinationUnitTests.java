import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Other.City;
import Other.Destination;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;


/**
 * Unit tests for Destination.
 */
public class DestinationUnitTests {
  public final ExpectedException expectedEx = ExpectedException.none();

  City Boston = new City("Boston", 0.8, 0.2);
  City Boston2 = new City("Boston", 0.8, 0.2);
  City NYC = new City("NYC", 0.7, 0.25);
  City vegas = new City("vegas", 0.3, 0.6);

  Destination BostonNYC = new Destination(Boston, NYC);
  Destination BostonNYC2 = new Destination(NYC, Boston);
  Destination NYCVegas = new Destination(vegas, NYC);
  Destination BostonVegas = new Destination(Boston, vegas);

  @Test (expected = IllegalArgumentException.class)
  public void testCantHaveSameCities() {
    expectedEx.expectMessage("Destination cannot have the same cities");
    Destination badDestination = new Destination(Boston, Boston2);
  }

  @Test
  public void testGetCity1() {
    assertEquals(Boston, BostonNYC2.getCity1());
  }

  @Test
  public void testGetCity2() {
    assertEquals(NYC, BostonNYC2.getCity2());
  }

  @Test
  public void testEqualDestinationsFalse() {
    assertFalse(NYCVegas.equals(BostonVegas));
  }

  @Test
  public void testEqualDestinationsTrue() {
    assertEquals(BostonNYC, BostonNYC2);
  }

  @Test
  public void testOrderingOfDestinations() {
    List<Destination> expected = new ArrayList<>();
    expected.add(BostonNYC);
    expected.add(BostonVegas);
    expected.add(NYCVegas);

    List<Destination> actual = new ArrayList<>();
    actual.add(BostonVegas);
    actual.add(NYCVegas);
    actual.add(BostonNYC);
    Collections.sort(actual);

    assertEquals(expected, actual);
  }
}
