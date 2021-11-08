import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.JsonStreamParser;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import map.ITrainMap;
import map_view.MapRenderer;
import org.junit.jupiter.api.Test;
import harnesses.XMap;

//TODO:
// - change current tests to pass
// - test more complicated multiple segments
// - test length indicator
public class TestMapRenderer {
  private static final int EXPECTED_CITY_RADIUS = 5;
  private static final Color EXPECTED_BACKGROUND_COLOR = Color.BLACK;
  private static final Color EXPECTED_CITY_COLOR = Color.YELLOW;
  private static final Color EXPECTED_CITY_TEXT_COLOR = Color.MAGENTA;
  private static final Color EXPECTED_LENGTH_INDICATOR_COLOR = Color.BLACK;

  public static ITrainMap readAndParseTestMap(String jsonFileName) {
    try {
      return XMap.trainMapFromJson(
          new JsonStreamParser(
                  new FileReader("Trains/Other/UnitTests/MapRenderedJsonInput/" + jsonFileName))
              .next());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void visualizeTrainMap(ITrainMap map) {
    BufferedImage mapView =
        new BufferedImage(
            map.getMapDimension().getWidth(),
            map.getMapDimension().getHeight(),
            BufferedImage.TYPE_INT_RGB);
    new MapRenderer().render(mapView.createGraphics(), map);
    renderImage(mapView);
  }

  @Test
  public void testMultipleConnections() {
    ITrainMap map = readAndParseTestMap("bos-sea-tex-duplicate-connections.json");
    //visualizeTrainMap(map);
  }

  private void renderImage(BufferedImage img) {
    JFrame frame = new JFrame();
    frame.getContentPane().setLayout(new FlowLayout());
    frame.getContentPane().add(new JLabel(new ImageIcon(img)));
    frame.pack();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(
        JFrame.EXIT_ON_CLOSE); // if you want the X button to close the app
    while (frame.isVisible()) {}
  }

  /** Test that an empty map produces a background of the proper size with the expected color */
  @Test
  public void testEmptyMap() {
    ITrainMap map = readAndParseTestMap("empty-map.json");

    // render map (400 x 200) onto larger image to test bounds
    BufferedImage mapView = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = mapView.createGraphics();
    g.setColor(Color.RED);
    g.fillRect(0, 0, 800, 800);
    new MapRenderer().render(mapView.createGraphics(), map);
    // renderImage(mapView);

    // within background should be black
    assertEquals(EXPECTED_BACKGROUND_COLOR, new Color(mapView.getRGB(0, 0)));
    assertEquals(EXPECTED_BACKGROUND_COLOR, new Color(mapView.getRGB(0, 199)));
    assertEquals(EXPECTED_BACKGROUND_COLOR, new Color(mapView.getRGB(200, 100)));
    assertEquals(EXPECTED_BACKGROUND_COLOR, new Color(mapView.getRGB(399, 0)));
    assertEquals(EXPECTED_BACKGROUND_COLOR, new Color(mapView.getRGB(399, 199)));

    // out of background should be red
    assertEquals(Color.RED, new Color(mapView.getRGB(0, 200)));
    assertEquals(Color.RED, new Color(mapView.getRGB(400, 0)));
  }

  @Test
  public void testIsolatedCities() {
    ITrainMap map = readAndParseTestMap("isolated-cities.json");

    // render map (400 x 200) onto larger image to test bounds
    BufferedImage mapView = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = mapView.createGraphics();
    g.setColor(Color.RED);
    g.fillRect(0, 0, 800, 800);
    new MapRenderer().render(mapView.createGraphics(), map);

    // Test of city in top-left corner, ensuring that it is a colored dot surrounded by the
    // background color
    assertTrue(
        containsRelativeColorAmount(mapView.getSubimage(0, 0, 5, 5), EXPECTED_CITY_COLOR, .75));
    assertFalse(
        containsRelativeColorAmount(mapView.getSubimage(5, 0, 10, 10), EXPECTED_CITY_COLOR, .01));
    assertFalse(
        containsRelativeColorAmount(mapView.getSubimage(0, 5, 5, 10), EXPECTED_CITY_COLOR, .1));

    // Basic test of city and city text in the middle of an image
    BufferedImage dodgeCityDot = mapView.getSubimage(196, 196, 8, 8);
    assertTrue(containsRelativeColorAmount(dodgeCityDot, EXPECTED_CITY_COLOR, .85));

    BufferedImage dodgeCityText = mapView.getSubimage(195, 185, 60, 10);
    assertTrue(containsRelativeColorAmount(dodgeCityText, EXPECTED_CITY_TEXT_COLOR, .2));

    // Behavior for a city that is in the corner - both the dot and the text appear off-screen for
    // the panel
    BufferedImage miamiDot = mapView.getSubimage(395, 395, 10, 10);
    assertTrue(containsRelativeColorAmount(miamiDot, EXPECTED_CITY_COLOR, .65));

    BufferedImage miamiDotOffScreen = mapView.getSubimage(401, 401, 5, 5);
    assertTrue(containsRelativeColorAmount(miamiDotOffScreen, EXPECTED_CITY_COLOR, .4));

    BufferedImage miamiText = mapView.getSubimage(395, 385, 30, 10);
    assertTrue(containsRelativeColorAmount(miamiText, EXPECTED_CITY_TEXT_COLOR, .2));

    BufferedImage miamiTextOffscreen = mapView.getSubimage(401, 385, 10, 10);
    assertTrue(containsRelativeColorAmount(miamiTextOffscreen, EXPECTED_CITY_TEXT_COLOR, .2));
  }

  @Test
  public void testSingleConnection() {
    ITrainMap map = readAndParseTestMap("single-connection.json");

    // render map (400 x 200) onto larger image to test bounds
    BufferedImage mapView = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = mapView.createGraphics();
    g.setColor(Color.RED);
    g.fillRect(0, 0, 800, 800);
    new MapRenderer().render(mapView.createGraphics(), map);

    // The city dot should be rendered over the connection, so over the entire dot, very little
    // should be white (connection color)
    BufferedImage seattleDot = mapView.getSubimage(95, 95, 10, 10);
    assertFalse(containsRelativeColorAmount(seattleDot, Color.WHITE, .03));

    // Two parts of the connection, both near either end, and should contain no text
    BufferedImage connectionSegment = mapView.getSubimage(105, 115, 10, 10);
    assertTrue(containsRelativeColorAmount(connectionSegment, Color.WHITE, .25));
    assertFalse(containsRelativeColorAmount(connectionSegment, EXPECTED_CITY_COLOR, .001));
    assertFalse(containsRelativeColorAmount(connectionSegment, EXPECTED_CITY_TEXT_COLOR, .001));

    BufferedImage connectionSegment2 = mapView.getSubimage(135, 175, 10, 10);
    assertTrue(containsRelativeColorAmount(connectionSegment2, Color.WHITE, .25));
    assertFalse(containsRelativeColorAmount(connectionSegment2, EXPECTED_CITY_COLOR, .001));
    assertFalse(containsRelativeColorAmount(connectionSegment2, EXPECTED_CITY_TEXT_COLOR, .001));

    // Test that length text for midpoint appears near the midpoint by checking for at least 10
    // text-color pixels in the 40 x 40 area
    BufferedImage segmentMidpoint = mapView.getSubimage(115, 130, 40, 40);
    assertTrue(colorReport(segmentMidpoint).get(EXPECTED_CITY_TEXT_COLOR) >= 10);
  }

  /**
   * Determines the number of pixels of the colors that appear in the entirety of the image.
   *
   * @param img
   * @return
   */
  private static Map<Color, Integer> colorReport(BufferedImage img) {
    Map<Color, Integer> result = new HashMap<>();
    for (int x = 0; x < img.getWidth(); x += 1) {
      for (int y = 0; y < img.getHeight(); y += 1) {
        Color pixelColor = new Color(img.getRGB(x, y));
        if (!result.containsKey(pixelColor)) {
          result.put(pixelColor, 0);
        } else {
          result.put(pixelColor, result.get(pixelColor) + 1);
        }
      }
    }
    return result;
  }

  private static Map<Color, Double> relativeColorReport(Map<Color, Integer> colorReport) {
    Map<Color, Double> result = new HashMap<>();
    double totalPixels = 0.0;
    for (Integer pixelsOfColor : colorReport.values()) {
      totalPixels += pixelsOfColor;
    }

    for (Entry<Color, Integer> oneColorReport : colorReport.entrySet()) {
      result.put(oneColorReport.getKey(), oneColorReport.getValue() / totalPixels);
    }
    return result;
  }

  private static Map<Color, Double> relativeColorReport(BufferedImage img) {
    return relativeColorReport(colorReport(img));
  }

  private static boolean containsRelativeColorAmount(BufferedImage img, Color color, double threshold) {
    Map<Color, Double> report = relativeColorReport(img);
    return report.containsKey(color) && report.get(color) >= threshold;
  }
}