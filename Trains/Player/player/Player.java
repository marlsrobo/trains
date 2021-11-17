package player;

import game_state.IPlayerGameState;
import game_state.RailCard;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import map.Destination;
import map.ITrainMap;
import referee.game_state.TrainsPlayerHand;
import strategy.IStrategy;
import action.TurnAction;
import test_utils.*;

/**
 * A player that only relies on one strategy, computes moves as they are requested, and otherwise
 * stores the minimal amount of information necessary for the strategy to make decisions.
 */
public class Player implements IPlayer {

    private static final int NUM_DESTINATIONS_TO_CHOOSE = 2;

    private final IStrategy playerStrategy;

    private ITrainMap mapInGame;

    private int numStartRails;

    private Map<RailCard, Integer> startingHand;

    private Set<Destination> chosenDestinations;

    private final ITrainMap mapToSubmit;

    /**
     * Constructs this player from the given strategy.
     *
     * @param strategy the strategy.
     */
    public Player(IStrategy strategy) {
        this.playerStrategy = strategy;
        this.mapToSubmit = TrainsMapUtils.createDefaultMap();
    }

    /**
     * Constructs this player from the strategy contained in the specified .class file. The .class
     * file should have only one strategy class contained in it, and should not have external
     * dependencies to ensure smooth construction.
     *
     * @param strategyFilePath the path to the .class file containing the strategy.
     * @throws RuntimeException if there is any issue loading the strategy from the class file.
     */
    public Player(String strategyFilePath) throws RuntimeException {
        this(strategyFilePath, TrainsMapUtils.createDefaultMap());
    }

    public Player(String strategyFilePath, ITrainMap mapToSubmit) {
        try {
            Class<IStrategy> strategyClass = loadStrategyClass(strategyFilePath);
            playerStrategy = strategyClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate strategy class: " + e.getMessage());
        }
      //  Objects.requireNonNull(mapToSubmit);
        this.mapToSubmit = mapToSubmit;
    }

    private static Class<IStrategy> loadStrategyClass(String strategyFilePath) throws IOException {
        return (Class<IStrategy>) new StrategyClassLoader().loadClassFromFile(strategyFilePath);
    }

    @Override
    public void setup(ITrainMap map, int numRails, List<RailCard> cards) {
        this.mapInGame = map;
        this.numStartRails = numRails;
        this.startingHand = new TrainsPlayerHand(cards).getHand();
    }

    @Override
    public Set<Destination> chooseDestinations(Set<Destination> options) {
        this.chosenDestinations =
            this.playerStrategy.chooseDestinations(
                options, NUM_DESTINATIONS_TO_CHOOSE, this.mapInGame, this.numStartRails,
                this.startingHand);
        return new HashSet<>(this.chosenDestinations);
    }

    @Override
    public TurnAction takeTurn(IPlayerGameState playerGameState) {
        return this.playerStrategy.takeTurn(
            playerGameState, this.mapInGame, new HashSet<>(this.chosenDestinations));
    }

    @Override
    public void receiveCards(List<RailCard> drawnCards) {
    }

    @Override
    public void winNotification(boolean thisPlayerWon) {
    }

    @Override
    public ITrainMap startTournament(boolean inTournament) {
        return this.mapToSubmit;
    }

    @Override
    public void resultOfTournament(boolean thisPlayerWon) {}
}
