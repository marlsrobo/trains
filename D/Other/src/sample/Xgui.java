package sample;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Xgui extends Application {

    private static final double DOT_SIZE = 5;
    private static final Color DOT_COLOR = Color.BLACK;
    private static final Color LINE_COLOR = Color.RED;
    private static final String CANVAS_COLOR = "orange";
    private static final Duration CLOSE_AFTER = Duration.seconds(3);

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Xgui");

        Pane canvas = new Pane();

        canvas.setStyle("-fx-background-color:" +  CANVAS_COLOR);

        JsonObject input = getInput();                  // parse all the input from STDIN as a JsonObject.

        int[][] positions = getPositions(input.get("nodes").getAsJsonArray());    // get the 'positions' from input
        int size = input.get("size").getAsInt();                    // get the 'size' from input

        canvas.getChildren().addAll(makeDots(positions));      // make the dots with positionsArray
        canvas.getChildren().addAll(makeLines(positions));     // make the lines with positionsArray

        Scene scene = new Scene(canvas, size, size);    // create new scene with given size
        primaryStage.setScene(scene);                   // set/show scene
        primaryStage.show();

        // Have the GUI close after a specified amount of time
        // Resource: https://stackoverflow.com/questions/27334455/how-to-close-a-stage-after-a-certain-amount-of-time-javafx
        PauseTransition delay = new PauseTransition(CLOSE_AFTER);
        delay.setOnFinished( event -> primaryStage.close() );
        delay.play();

    }

    private int[][] getPositions(JsonArray positions) {
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

    // TODO: getting JSON input from STDIN, turning into useful objects, then passing to start
    public static void main(String[] args) {
        launch(args);
    }

    // return a list of dots, one dot is created for each position specified
    private List<Circle> makeDots(int[][] positions) {
        List<Circle> dots = new ArrayList<Circle>();
        for (int[] p : positions) {
            Circle dot = makeDot(p[0], p[1]);
            dots.add(dot);
        }
        return dots;
    }

    // return a single dot whose coordinates are specified by x and y arguments
    private Circle makeDot(double x, double y) {
        Circle dot = new Circle();
        dot.setCenterX(x);
        dot.setCenterY(y);
        dot.setRadius(DOT_SIZE);
        dot.setStroke(DOT_COLOR);
        dot.setFill(DOT_COLOR);
        return dot;
    }

    // return a list of lines that connect all of the positions specified
    // TODO: TRY TO OPTIMIZE LOOP
    private List<Line> makeLines(int[][] positions) {
        List<Line> lines = new ArrayList<Line>();
        for (int pos1 = 0; pos1 < positions.length; pos1++) {
            for (int pos2 = pos1; pos2 < positions.length; pos2++) {
                Line line = makeLine((double)positions[pos1][0], (double)positions[pos2][0],
                        (double)positions[pos1][1], (double)positions[pos2][1]);
                lines.add(line);
            }
        }
        return lines;
    }

    // return a single line whose endpoints are specified by the coordinate arguments
    private Line makeLine(double xStart, double xEnd, double yStart, double yEnd) {
        Line line = new Line();
        line.setStartX(xStart);
        line.setEndX(xEnd);
        line.setStartY(yStart);
        line.setEndY(yEnd);
        line.setStroke(LINE_COLOR);
        line.setFill(LINE_COLOR);
        return line;
    }
}
