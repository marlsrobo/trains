package test_utils;

import game_state.RailCard;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import map.City;
import map.Destination;
import map.ICity;
import map.IRailConnection;
import map.ITrainMap;
import map.RailColor;
import map.RailConnection;
import map.TrainMap;
import utils.Constants;
import utils.UnorderedPair;

/**
 * Contains useful default values for the abstracted setup functions for a game of Trains.
 */
public class TrainsMapUtils {

    /**
     * Creates a reasonable default map for a game of Trains. Contains enough destinations for games
     * of up to 8 players.
     */
    public static ITrainMap createDefaultMap() {
        ICity boston = new City("Boston", 0, 0);
        ICity nyc = new City("NYC", 1, 1);
        ICity chicago = new City("chicago", 0.5, 0.5);
        ICity washington = new City("washington", 0, 1);
        ICity texas = new City("texas", 1, 0);
        ICity lincoln = new City("lincoln", 0.5, 0);
        ICity mystic = new City("mystic", 0, 0.5);
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

    /**
     * The default destination provider for games of Trains. A destination provider function accepts
     * an ITrainMap, and returns the order to hand the destinations in that map to players.
     * <p>
     * This deck provider shuffles the order of all destinations in the map.
     *
     * @param map The map for the game of Trains.
     * @return The destinations in the the map, in the order that they should be provided to
     * players.
     */
    public static List<Destination> defaultDestinationProvider(ITrainMap map) {
        List<Destination> result =
            map.getAllPossibleDestinations().stream()
                .map((pair) -> new Destination(pair))
                .collect(Collectors.toList());
        Collections.shuffle(result);
        return result;
    }

    /**
     * The default deck provider for games of Trains. A deck provider function accepts no arguments,
     * and returns a list of RailCards in the order that they should be provided to players when
     * they draw from the deck.
     * <p>
     * This deck provider returns 250 cards randomly chosen from among the possible RailCard
     * values.
     *
     * @return The destinations in the the map, in the order that they should be provided to
     * players.
     */
    public static List<RailCard> defaultDeckSupplier() {
        List<RailCard> result = new ArrayList<>();
        Random cardSelector = new Random();
        RailCard[] railCardOptions = RailCard.values();
        for (int cardNumber = 0; cardNumber < Constants.DECK_SIZE; cardNumber += 1) {
            result.add(railCardOptions[cardSelector.nextInt(railCardOptions.length)]);
        }
        return result;
    }

    /**
     * The default map selector for games of Trains. A map selector function accepts a non-empty
     * list of ITrainMaps, and returns the one that * should be used for all games in this
     * tournament.that they should be provided to players when they draw from the deck.
     * <p>
     * This map selector chooses the first map that it is given.
     *
     * @return The destinations in the the map, in the order that they should be provided to
     * players.
     */
    public static ITrainMap defaultMapSelector(List<ITrainMap> maps) {
        if (maps.isEmpty()) {
            throw new IllegalArgumentException("Must be given at least 1 map to select");
        }
        return maps.get(0);
    }

    public static boolean sameMap(ITrainMap map1, ITrainMap map2) {
        boolean sameCities = map1.getCities().equals(map2.getCities());
        boolean sameConnections = map1.getRailConnections().equals(map2.getRailConnections());

        return sameCities && sameConnections;
    }
}
