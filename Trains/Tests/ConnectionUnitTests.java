import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Other.City;
import Other.Connection;
import Other.ConnectionColor;

import javafx.scene.paint.Color;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

/**
 * Unit tests for Connection.
 */
public class ConnectionUnitTests {

  @Rule
  public final ExpectedException expectedEx = ExpectedException.none();

  private final City boston = new City("boston", 0, 1);
  private final City Boston = new City("Boston", 1, 0);
  private final City nyc = new City("nyc", 0.8, 0.5);
  private final City NYC = new City("NYC", 0.2, 0.4);

  private final City losAngeles = new City("Los Angeles", 0.2, 0.7);

  private final Connection bostonNYCRed5 = new Connection(ConnectionColor.red, 5, boston, NYC);
  private final Connection bostonNYCGreen5 = new Connection(ConnectionColor.green, 5, boston, NYC);
  private final Connection bostonNYCGreen3 = new Connection(ConnectionColor.green, 3, boston, NYC);
  private final Connection BostonNYCBlue3 = new Connection(ConnectionColor.blue, 3, Boston, NYC);
  private final Connection NYCla = new Connection(ConnectionColor.white, 4, NYC, losAngeles);

  @Test
  public void testBadConnectionSameExactCities() {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("Connection can't have duplicate cities");
    Connection badConnection = new Connection(ConnectionColor.blue, 4, boston, boston);
  }

  @Test
  public void testBadConnectionSameCitiesSameName() {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("Connection can't have duplicate cities");
    Connection badConnection = new Connection(ConnectionColor.blue, 5, boston,
            new City("boston", 0.5, 0.5));
  }

  @Test
  public void testBadConnectionSameCitiesDiffNameSameLocation() {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("Connection can't have duplicate cities");
    Connection badConnection = new Connection(ConnectionColor.blue, 3, boston,
            new City("fakeName", 0, 1));
  }

  @Test
  public void testIllegalLength() {
    expectedEx.expect(IllegalArgumentException.class);
    expectedEx.expectMessage("Length must be 3, 4, or 5");
    Connection badConnection = new Connection(ConnectionColor.blue, 1, boston, NYC);
  }

  @Test
  public void testGetCities() {
    Set<City> cities = new HashSet<>();
    cities.add(boston);
    cities.add(NYC);

    assertEquals(cities, bostonNYCGreen3.getCities());
  }

  @Test
  public void testHasSameCitiesFalse() {
    assertFalse(bostonNYCRed5.hasSameCities(NYCla));
  }

  @Test
  public void testHasSameCitiesTrue() {
    assertTrue(bostonNYCRed5.hasSameCities(bostonNYCGreen5));
  }

  @Test
  public void testGetLength() {
    assertEquals(5, bostonNYCRed5.getLength());
  }

  @Test
  public void testGetColor() {
    assertEquals(Color.RED, bostonNYCRed5.getColor());
  }

  @Test
  public void testGetConnectionColor() {
    assertEquals(ConnectionColor.red, bostonNYCRed5.getConnectionColor());
  }

  @Test
  public void testEqualsFalse1() {
    assertFalse(BostonNYCBlue3.equals(bostonNYCGreen5));
  }

  @Test
  public void testEqualsFalse2() {
    assertFalse(NYCla.equals(BostonNYCBlue3));
  }

  @Test
  public void testEqualsSameConnection() {
    assertTrue(bostonNYCRed5.equals(new Connection(ConnectionColor.red, 5, boston, NYC)));
  }

  @Test
  public void testEqualsSameColorCitiesDiffLength() {
    assertTrue(bostonNYCGreen3.equals(bostonNYCGreen5));
  }

  @Test
  public void testConnectionLexOrdering() {
    List<Connection> expected = new ArrayList<>();
    expected.add(BostonNYCBlue3);
    expected.add(bostonNYCGreen3);
    expected.add(bostonNYCGreen5);
    expected.add(bostonNYCRed5);

    List<Connection> unsorted = new ArrayList<>();
    unsorted.add(bostonNYCGreen5);
    unsorted.add(bostonNYCRed5);
    unsorted.add(BostonNYCBlue3);
    unsorted.add(bostonNYCGreen3);

    Collections.sort(unsorted);
    List<Connection> actual = new ArrayList<>(unsorted);

    assertEquals(expected, actual);
  }
}