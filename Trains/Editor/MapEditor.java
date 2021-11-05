package Editor;

import java.util.*;

import Common.TrainsMap;
import Other.City;
import Other.Connection;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Visualizer for our Map. Draws the cities as dots with the city names and the connections
 * as segmented, colored lines between the dots.
 */
public class MapEditor extends Application {
  protected static final double CITY_SIZE = 12;
  protected static final double FONT_SIZE = 15;
  protected static final Color CITY_COLOR = Color.RED;
  protected static final Color NAME_COLOR = Color.WHITE;
  protected static final Color CANVAS_COLOR = Color.BLACK;
  protected static final int OFFSET_MULT = 5;
  protected static final double LINE_WIDTH = 2;

  protected static TrainsMap map;
  protected Pane canvas;

  // if setTimeout isn't called by another class before launching the Javafx app, don't time out.
  private static PauseTransition delay = new PauseTransition(Duration.INDEFINITE);

  /***
   * Setting the Map.
   * @param m is the map given to the MapEditor
   */
  public static void setMap(TrainsMap m) {
    Objects.requireNonNull(m);
    map = m;
  }

  /**
   * Sets the delay time of the timeout for this visualization
   * @param secondsToTimeout the seconds to close the window after initialization
   */
  public static void setTimeout(int secondsToTimeout) {
    delay = new PauseTransition(Duration.seconds(secondsToTimeout));
  }

  /***
   * Launches JavaFX application
   * @param args n/a
   */
  public static void main(String[] args) {
    launch(args);
  }

  /***
   * Initializes the canvas, draws cities and connections on it, and sets the timeout.
   */
  @Override
  public void start(Stage primaryStage) throws Exception{
    primaryStage.setTitle("Trains");
    canvas = new Pane();
    canvas.setBackground(new Background(new BackgroundFill(CANVAS_COLOR,
            new CornerRadii(0), Insets.EMPTY)));

    drawConnections();
    drawCities();

    Scene scene = new Scene(canvas, map.getWidth(), map.getHeight());
    primaryStage.setScene(scene);
    primaryStage.show();

    delay.setOnFinished( event -> primaryStage.close() );
    delay.play();
  }

  /**
   * Draws the connections from the TrainsMap unto the canvas.
   */
  protected void drawConnections() {
    Map<Connection, Integer> connectionOffsets = getOffSetsForConnections();
    // Maintains connection drawing order.
    ArrayList<Connection> connections = new ArrayList<>(map.getConnections());

    for (Connection connection : connections) {
      drawConnection(connection, connectionOffsets.get(connection));
    }
  }

  /**
   * Gets the count of each connection (if two connections have the same cities, the count gets incremented)
   * @return a Map of Connection to Integer representing the count of each connection between the same endpoints.
   */
  public Map<Connection, Integer> getOffSetsForConnections() {
    Set<Connection> connections = map.getConnections();
    Set<Connection> notSeenConnections = map.getConnections();

    HashMap<Connection, Integer> connectionOffsets = new HashMap<>();

    for (Connection c : notSeenConnections) {
      int count = 0;
      connectionOffsets.put(c, count);
      count++;
      for (Connection c2 : connections) {
        if (c.hasSameCities(c2) && !c.equals(c2)) {
          connectionOffsets.put(c2, count);
          count++;
        }
      }
    }
    return connectionOffsets;
  }

  /**
   * Draws a Connection as a line and segments it by drawing circles the same color as the background of the canvas.
   * If there are multiple connections between the same cities, then the placement of the line is adjusted by the offset.
   * @param connection a Connection to be drawn on the map
   * @param offset the offset by which to position the line
   */
  private void drawConnection(Connection connection, double offset) {
    ArrayList<Double> xValues = new ArrayList<>();
    ArrayList<Double> yValues = new ArrayList<>();

    for (City c : connection.getCities()) {
      xValues.add(c.getAbsoluteX(map.getWidth()));
      yValues.add(c.getAbsoluteY(map.getHeight()));
    }
    double xStart = xValues.get(0);
    double xEnd = xValues.get(1);
    double yStart = yValues.get(0);
    double yEnd = yValues.get(1);

    offset = (Math.ceil(offset / 2) * OFFSET_MULT) * Math.pow(-1, offset);

    Line line = makeNonSegmentedLine(xStart + offset, xEnd + offset,
            yStart + offset, yEnd + offset, connection.getColor());
    line.setStrokeWidth(LINE_WIDTH);

    ArrayList<Circle> blackDots = makeSegmentedDots(xStart + offset, xEnd + offset,
            yStart + offset, yEnd + offset, connection.getLength());

    canvas.getChildren().add(line);
    canvas.getChildren().addAll(blackDots);
  }

  /***
   * Draws all the cities on the canvas as a Circle of constant size and color, and draws
   * the name for each city directly on top of the Circle corresponding to the city.
   */
  protected void drawCities() {
    Set<City> cities = map.getCities();

    for (City c : cities) {
      Circle dot = makeDot(c.getAbsoluteX(map.getWidth()), c.getAbsoluteY(map.getHeight()),
              CITY_SIZE, CITY_COLOR);
      Text name = makeName(c.getName(), c.getAbsoluteX(map.getWidth()),
              c.getAbsoluteY(map.getHeight()));

      canvas.getChildren().add(dot);
      canvas.getChildren().add(name);
    }
  }

  /**
   * Returns a Circle whose coordinates are specified by x and y, whose size is radius, and
   * is filled with the specified color
   * @param x x position
   * @param y y position
   * @param radius radius of the dot
   * @param color color of the dot
   * @return a Circle representing a "dot" on the Map
   */
  private Circle makeDot(double x, double y, double radius, Color color) {
    Circle dot = new Circle();
    dot.setCenterX(x);
    dot.setCenterY(y);
    dot.setRadius(radius);
    dot.setStroke(color);
    dot.setFill(color);
    return dot;
  }

  /**
   * Returns a Text for the name of the specified city in the given coordinates
   * @param cityName name of city to display as text
   * @param x position of city
   * @param y position of city
   * @return the Text representing the city's name
   */
  private Text makeName(String cityName, double x, double y) {
    Text name = new Text();
    name.setText(cityName);
    name.setX(x);
    name.setY(y);
    name.setFont(Font.font("Verdana", FONT_SIZE));
    name.setFill(NAME_COLOR);
    name.setX(name.getX() - name.getLayoutBounds().getWidth() / 2);
    name.setY(name.getY() + name.getLayoutBounds().getHeight() / 4);
    return name;
  }

  /***
   * Returns a Line with the given color that starts at startX, startY and ends at endX, endY.
   * @param startX the starting x coord
   * @param endX the target x coord
   * @param startY the starting y coord
   * @param endY the target y coord
   * @param color the color of the line
   * @return a line representing a connection
   */
  private Line makeNonSegmentedLine(double startX, double endX, double startY, double endY, Color color) {
    Line line = new Line();
    line.setStartX(startX);
    line.setEndX(endX);
    line.setStartY(startY);
    line.setEndY(endY);
    line.setStroke(color);
    line.setFill(color);
    return line;
  }

  /***
   * Returns a list of small circles to segment the line into the given number of segments
   * @param startX the starting x coord
   * @param endX the target x coord
   * @param startY the starting y coord
   * @param endY the target y coord
   * @param segments the number of pieces to break up the line
   * @return a list of Circle along which the line will be segmented.
   */
  private ArrayList<Circle> makeSegmentedDots(double startX, double endX, double startY, double endY,
                                              int segments) {
    ArrayList<Circle> dots = new ArrayList<>();

    double xSlope = endX - startX;
    double ySlope = endY - startY;

    double xPos = (xSlope / segments);
    double yPos = (ySlope / segments);

    for (int i = 0; i < segments; i++) {

      Circle dot = makeDot(startX + xPos, startY + yPos, CITY_SIZE / 5, CANVAS_COLOR);
      dots.add(dot);

      startX = startX + xPos;
      startY = startY + yPos;
    }

    return dots;
  }
}
