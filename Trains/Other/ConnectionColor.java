package Other;

import java.util.Objects;

import javafx.scene.paint.Color;

/***
 * Represents the color of a Connection
 */
public enum ConnectionColor {
    red(Color.RED),
    blue(Color.BLUE),
    green(Color.GREEN),
    white(Color.WHITE);

    private final Color color;

    /***
     * Constructor for a ConnectionColor
     * @param color the color for a Connection
     */
    ConnectionColor(Color color) {
        Objects.requireNonNull(color);
        this.color = color;
    }

    /***
     * Getter for the color of this ConnectionColor
     * @return the Color of this ConnectionColor
     */
    public Color getColor() {
        return this.color;
    }

    /***
     * Returns a connections color based on a given color name
     * @param colorName the name of the color
     * @return a ConnectionColor corresponding to the given name
     */
    public static ConnectionColor stringToColor(String colorName) {
        switch (colorName.toLowerCase()) {
            case "red":
                return ConnectionColor.red;
            case "blue":
                return ConnectionColor.blue;
            case "green":
                return ConnectionColor.green;
            case "white":
                return ConnectionColor.white;
            default:
                throw new IllegalArgumentException("Color must be one of red, blue, green, or white");
        }
    }

    /***
     * Returns a connections color based on a given number
     * @param colorNum the number of the color
     * @return a ConnectionColor corresponding to the given number
     */
    public static ConnectionColor numberToColor(int colorNum) {
        switch (colorNum) {
            case 0:
                return ConnectionColor.red;
            case 1:
                return ConnectionColor.blue;
            case 2:
                return ConnectionColor.green;
            case 3:
                return ConnectionColor.white;
            default:
                throw new IllegalArgumentException("Color must be one of red, blue, green, or white");
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case red:
                return "red";
            case blue:
                return "blue";
            case green:
                return "green";
            case white:
                return "white";
            default:
                throw new IllegalArgumentException("There is not an enum of this color");
        }
    }
}
