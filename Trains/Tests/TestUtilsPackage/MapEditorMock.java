package TestUtilsPackage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import javax.imageio.ImageIO;

import Editor.MapEditor;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Mock MapEditor that generates and saves an image of the Map visualization.
 */
public class MapEditorMock extends MapEditor {
  private static String fileName;

  public static void setFileName(String name) {
    Objects.requireNonNull(name);
    fileName = name;
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("Trains");
    canvas = new Pane();
    canvas.setBackground(new Background(new BackgroundFill(CANVAS_COLOR,
            new CornerRadii(0), Insets.EMPTY)));

    drawConnections();
    drawCities();
    Scene scene = new Scene(canvas, map.getWidth(), map.getHeight());
    primaryStage.setScene(scene);
    primaryStage.show();
    saveScreenshotOfAppToFile(canvas, fileName);
  }

  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Saves a screenshot of the canvas of the Map being drawn to a certain file location
   * @param canvas the canvas (Pane) to be saved as an image file
   * @param imageName the name to save the file as
   * @throws IOException throws error if it can't write to the file
   */
  private static void saveScreenshotOfAppToFile(Pane canvas, String imageName) throws IOException {
    Path resourceDirectory = Paths.get("Trains", "Tests","MapViews", "Actual", imageName);
    WritableImage wImage = canvas.snapshot(new SnapshotParameters(), null);
    File outputFile = new File(String.valueOf(resourceDirectory));
    ImageIO.write(SwingFXUtils.fromFXImage(wImage, null), "png", outputFile);
  }
}
