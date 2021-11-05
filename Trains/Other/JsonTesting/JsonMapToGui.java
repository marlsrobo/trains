package Other.JsonTesting;

import com.google.gson.JsonStreamParser;

import Common.TrainsMap;
import Editor.MapEditor;
import Other.Utils;

/**
 * Take a json representation of a TrainsMap from STDIN and displays it
 */
public class JsonMapToGui {

  /**
   * Main method to initialize the visualization of a TrainsMap which is created from the
   * parsed JSON from STDIN
   * @param args never used
   */
  public static void main(String[] args) {
    // parse JSON string
    JsonStreamParser parsedJson = new JsonStreamParser(Utils.getJson());

    // getting the map and converting to our data representation
    TrainsMap map = JsonToTrains.jsonToMap(parsedJson.next().getAsJsonObject());

    // visualize the map
    MapEditor.setMap(map);
    MapEditor.setTimeout(10);
    MapEditor.main(args);
  }
}
