package test_utils;

import java.util.HashSet;
import java.util.Set;
import map.City;
import map.ICity;
import map.IRailConnection;
import map.ITrainMap;
import map.RailColor;
import map.RailConnection;
import map.TrainMap;
import utils.UnorderedPair;

public class TrainsMapUtils {

    /**
     * Mention that this only works for max 8 players when writing comment
     */
    public static ITrainMap createDefaultMap() {
        ICity boston = new City("Boston", 0, 0);
        ICity nyc = new City("NYC", 0, 0);
        ICity chicago = new City("chicago", 0, 0);
        ICity washington = new City("washington", 0, 0);
        ICity texas = new City("texas", 0, 0);
        ICity lincoln = new City("lincoln", 0, 0);
        ICity mystic = new City("mystic", 0, 0);
        Set<ICity> cities = new HashSet<>();
        cities.add(boston);
        cities.add(nyc);
        cities.add(chicago);
        cities.add(washington);
        cities.add(texas);
        cities.add(lincoln);
        cities.add(mystic);

        IRailConnection connection1 = new RailConnection(new UnorderedPair<>(boston, nyc), 3,
            RailColor.BLUE);
        IRailConnection connection2 = new RailConnection(new UnorderedPair<>(chicago, nyc), 5,
            RailColor.RED);
        IRailConnection connection3 = new RailConnection(new UnorderedPair<>(washington, nyc), 3,
            RailColor.GREEN);
        IRailConnection connection4 = new RailConnection(new UnorderedPair<>(texas, nyc), 3,
            RailColor.BLUE);
        IRailConnection connection5 = new RailConnection(new UnorderedPair<>(lincoln, nyc), 4,
            RailColor.WHITE);
        IRailConnection connection6 = new RailConnection(new UnorderedPair<>(boston, lincoln), 3,
            RailColor.BLUE);
        IRailConnection connection7 = new RailConnection(new UnorderedPair<>(lincoln, texas), 5,
            RailColor.RED);
        IRailConnection connection8 = new RailConnection(new UnorderedPair<>(mystic, lincoln), 4,
            RailColor.BLUE);
        Set<IRailConnection> rails = new HashSet<>();
        rails.add(connection1);
        rails.add(connection2);
        rails.add(connection3);
        rails.add(connection4);
        rails.add(connection5);
        rails.add(connection6);
        rails.add(connection7);
        rails.add(connection8);

        return new TrainMap(cities, rails);
    }
}
