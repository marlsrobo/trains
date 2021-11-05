package Other;

import java.util.Scanner;

/**
 * A utils class containing useful methods
 */
public class Utils {

  public final static int NOT_ACQUIRED_CONNECTION_STATUS = -1;

  public final static int DESTINATIONS_PER_PLAYER = 2;

  /**
   * Gets the json from STDIN and returns it as a string
   * @return the json representation as a String
   */
  public static String getJson() {
    Scanner scanner = new Scanner(System.in);
    StringBuilder jsonString = new StringBuilder();

    while (scanner.hasNext()) {
      jsonString.append(scanner.next());
    }
    scanner.close();
    return jsonString.toString();
  }

  /**
   * Converts the given coordinate value from an absolute value to a relative one
   * @param absoluteCoord the absolute coordinate
   * @param widthOrHeight the width or height that the coord will be placed on
   * @return the relative coordinate value
   */
  public static double absoluteToRelativeCoord(double absoluteCoord, int widthOrHeight) {
    return absoluteCoord / widthOrHeight;
  }

}
