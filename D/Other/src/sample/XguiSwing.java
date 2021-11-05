package sample;

import com.google.gson.*;

import java.awt.*;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class XguiSwing extends JPanel{

  private static final int DOT_SIZE = 10;
  private static final Color DOT_COLOR = Color.BLACK;
  private static final Color LINE_COLOR = Color.RED;
  private static final Color CANVAS_COLOR = Color.ORANGE;
  private static final int MAX_CANVAS_SIZE = 800;
  // milliseconds
  private static final long CLOSE_AFTER = 3000;

  private static int[][] positions;
  private static int size;

  public static void main(String[] args) throws InterruptedException {

    JsonObject input = getInput();                  // parse all the input from STDIN as a JsonObject.

    positions = getPositions(input.get("nodes").getAsJsonArray());    // get the 'positions' from input
    size = input.get("size").getAsInt();                    // get the 'size' from input

    JFrame frame = new JFrame("Xgui");
    XguiSwing gui = new XguiSwing();
    frame.add(gui, BorderLayout.CENTER);
    frame.setSize(MAX_CANVAS_SIZE, MAX_CANVAS_SIZE);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);

    Thread.sleep(CLOSE_AFTER);
    frame.setVisible(false);
    frame.dispose();

  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    drawBackground(g, size);
    drawDots(g, positions);
    drawLines(g, positions);
  }

  private static int[][] getPositions(JsonArray positions) {
    int arraySize = positions.size();

    int[][] positionsArray = new int[arraySize][2];
    for(int i = 0; i < arraySize; i ++) {  // go through the positions JsonArray and convert it to an int[][]
      for (int j = 0; j < 2; j++) {      // every element in the array has two coordinates (from defition of Posn)
        positionsArray[i][j] = positions.get(i).getAsJsonArray().get(j).getAsInt();
      }
    }
    return positionsArray;
  }

  private static JsonObject getInput() {
    // getting STDIN input and converting to string

    Scanner scanner = new Scanner(System.in);
    StringBuilder jsonString = new StringBuilder();

    while (scanner.hasNext()) {
      jsonString.append(scanner.next());
    }

    scanner.close();

    // parse JSON string
    JsonElement parsedJson = JsonParser.parseString(jsonString.toString());

    return parsedJson.getAsJsonObject();
  }

  // return a single line whose endpoints are specified by the coordinate arguments
  private void drawBackground(Graphics g, int sideLength) {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setColor(CANVAS_COLOR);
    g2d.fillRect(0, 0, sideLength, sideLength);
  }


  // return a list of dots, one dot is created for each position specified
  private void drawDots(Graphics g, int[][] positions) {
    for (int[] p : positions) {
      drawDot(g, p[0], p[1]);
    }
  }

  // return a single dot whose coordinates are specified by x and y arguments
  private void drawDot(Graphics g, int x, int y) {
    Graphics2D g2d = (Graphics2D) g;
    int x_center = x - (DOT_SIZE / 2);
    int y_center = y - (DOT_SIZE / 2);
    g2d.setColor(DOT_COLOR);
    g2d.fillOval(x_center, y_center, DOT_SIZE, DOT_SIZE);
  }

  // return a list of lines that connect all of the positions specified
  private void drawLines(Graphics g, int[][] positions) {
    for (int pos1 = 0; pos1 < positions.length; pos1++) {
      for (int pos2 = pos1; pos2 < positions.length; pos2++) {
        drawLine(g, positions[pos1][0], positions[pos2][0], positions[pos1][1], positions[pos2][1]);
      }
    }
  }

  // return a single line whose endpoints are specified by the coordinate arguments
  private void drawLine(Graphics g, int xStart, int xEnd, int yStart, int yEnd) {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setColor(LINE_COLOR);
    g2d.drawLine(xStart, yStart, xEnd, yEnd);
  }
}
