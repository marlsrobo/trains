package TestUtilsPackage;

import Common.TrainsMap;
import Editor.MapEditor;
import Other.City;
import Other.Connection;
import Other.ConnectionColor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * MapEditor runner to be used to generate visualizations and images if we need to (use mock for that)
 */
public class MapEditorRunner {

    /***
     * Creates a Map with cities and connections.
     * @return a Map
     */
    private static TrainsMap createMap1() {
        City boston = new City("Boston", 0.9, 0.1);
        City newYork = new City("New York City", 0.8, 0.2);
        City losAngeles = new City("Los Angeles", 0.1, 0.7);
        City miami = new City("Miami", 0.9, 0.9);
        City houston = new City("Houston", 0.5, 0.85);

        Set<City> pair1 = new HashSet<>();
        Set<City> pair2 = new HashSet<>();
        Set<City> pair3 = new HashSet<>();
        Set<City> pair4 = new HashSet<>();

        Set<City> cities = new HashSet<>();
        cities.add(boston);
        cities.add(newYork);
        cities.add(losAngeles);
        cities.add(miami);
        cities.add(houston);

        pair1.add(boston);
        pair1.add(newYork);

        pair2.add(losAngeles);
        pair2.add(newYork);

        pair3.add(boston);
        pair3.add(miami);

        pair4.add(miami);
        pair4.add(losAngeles);

        Set<Connection> connections = new HashSet<>();
        connections.add(new Connection(ConnectionColor.red, 3, pair1));
        connections.add(new Connection(ConnectionColor.blue, 5, pair2));
        connections.add(new Connection(ConnectionColor.white, 4, pair3));
        connections.add(new Connection(ConnectionColor.red, 4, pair3));
        connections.add(new Connection(ConnectionColor.blue, 4, pair3));
        connections.add(new Connection(ConnectionColor.green, 5, pair4));
        connections.add(new Connection(ConnectionColor.red, 3, pair4));
        connections.add(new Connection(ConnectionColor.green, 5, pair2));
        connections.add(new Connection(ConnectionColor.white, 4, pair2));
        connections.add(new Connection(ConnectionColor.red, 4, pair2));


        return new TrainsMap(cities, connections, 800, 800);
    }


    /**
     * Launches the JavaFX mock application
     * @param args n/a
     */
    public static void main(String[] args) throws IOException {
        MapEditor.setMap(createMap1());
        MapEditor.main(args);
      //  MapEditorMock.setFileName("image1.png");
      //  MapEditorMock.setMap(createMap1());
      //  MapEditorMock.main(args);
    }
}