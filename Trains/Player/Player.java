package Player;

import Common.PlayerGameState;
import Common.TrainsMap;
import Other.*;
import Other.Action.Action;
import Other.Player.PlayerAPI;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A Player component for the Trains game that performs the mechanical tasks.
 */
public class Player implements PlayerAPI {

    private final int playerId;
    private PlayerGameState gameState;
    private Strategy strategy;
    private boolean winner = false;

    /**
     * Constructor for the Player component.
     * @param playerId the id of this player
     * @param gameState the current PlayerGameState for this Player.
     * @param strategyFilePath the file path to the specific Strategy that this Player wants to use.
     */
    public Player(int playerId, PlayerGameState gameState, String strategyFilePath) {
        Objects.requireNonNull(gameState);
        Objects.requireNonNull(strategyFilePath);
        if (playerId == Utils.NOT_ACQUIRED_CONNECTION_STATUS) {
            throw new IllegalArgumentException("Player id cannot be " + Utils.NOT_ACQUIRED_CONNECTION_STATUS);
        }
        this.playerId = playerId;
        setStrategyFromFilePath(strategyFilePath);
        this.gameState = gameState;
    }

    /**
     * Finds the class name for a Strategy given a string file path, and creates a new instance of that Strategy class.
     * @param filePath the path to the specific Strategy implementation class.
     * @return a new Strategy of the type specified by the filepath
     */
    private void setStrategyFromFilePath(String filePath) {
        // Replace all "/" with "." first!
        String newFileName = filePath.replaceAll("/", ".");
        // The name of the file (+ the package) is everything in the directory structure under "Trains"
        String expectedParentDirectory = "Trains.";

        // grab the file name from everything after "Trains"
        String classFileName = newFileName.substring(newFileName.lastIndexOf(expectedParentDirectory)
                + expectedParentDirectory.length());

        // Take out the ".java"
        String className = classFileName.substring(0, classFileName.indexOf(".java"));

        try {
            // Load the class with the specified name (including its package)
            Class<?> strategy = ClassLoader.getSystemClassLoader().loadClass(className);
            this.strategy = (Strategy)strategy.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up this player with the basic game pieces
     * @param map   the map of this game of Trains
     * @param rails the number of rails available to this player
     * @param cards the hand of cards available to this player (number per type of colored cards)
     */
    @Override
    public void setup(TrainsMap map, int rails, Map<ConnectionColor, Integer> cards) {
        PlayerInventory inventory = new PlayerInventory(cards, rails);
        this.gameState = new PlayerGameState(map, this.gameState.getOrderOfPlayerTurns(), this.gameState.getCurrentPlayer(),
                this.gameState.getConnectionsStatus(), inventory);
    }

    /**
     * Asks this player to pick some destinations for the game relative to the given map
     * @param destinations the set of destinations for the player to choose from
     * @return Set<Destination> the destinations that the Player does not want.
     */
    @Override
    public Set<Destination> pick(Set<Destination> destinations) {
        // have the strategy pick the 2 destinations
        Set<Destination> pickedDestinations = strategy.pickDestinations(destinations);

        // create the new inventory and game state for this player
        PlayerInventory inventory = new PlayerInventory(this.gameState.getCardsInHand(),
                this.gameState.getRailsInHand(), pickedDestinations);
        this.gameState = new PlayerGameState(this.gameState.getMap(), this.gameState.getOrderOfPlayerTurns(),
                this.gameState.getCurrentPlayer(), this.gameState.getConnectionsStatus(), inventory);

        // remove the picked destinations from the ones provided by the Referee and return the remaining destinations
        destinations.removeAll(pickedDestinations);
        return destinations;
    }

    /**
     * Grants this player a turn
     * @param gameState the state of the game for this player
     * @return an Action of either more_cards or acquired (acquired contains a Connection the player wants to acquire)
     */
    @Override
    public Action play(PlayerGameState gameState) {
        this.gameState = gameState;
        return strategy.pickTurn(this.gameState);
    }

    /**
     * Hands this player some cards
     * @param cards the cards that are given to this player
     */
    @Override
    public void more(Map<ConnectionColor, Integer> cards) {
        Map<ConnectionColor, Integer> cardsInHand = this.gameState.getCardsInHand();
        for (ConnectionColor color : cards.keySet()) {
            cardsInHand.put(color, cardsInHand.get(color) + cards.get(color));
        }

        // create the new inventory and game state for this player
        PlayerInventory inventory = new PlayerInventory(cardsInHand, this.gameState.getRailsInHand(),
                this.gameState.getDestinations());
        this.gameState = new PlayerGameState(this.gameState.getMap(), this.gameState.getOrderOfPlayerTurns(),
                this.gameState.getCurrentPlayer(), this.gameState.getConnectionsStatus(), inventory);
    }

    /**
     * Tells this player if they win. Updates the "winner" field.
     * @param winner whether the player has won the game or not.
     */
    @Override
    public void win(boolean winner) {
        this.winner = winner;
    }

    /**
     * Getter for this Player's ID
     * @return the ID of this player represented as an integer
     */
    public int getPlayerID() {
        return this.playerId;
    }

    /**
     * Getter for this Player's game state
     * @return the current PlayerGameState of this Player.
     */
    public PlayerGameState getGameState() {
        return this.gameState;
    }

    /**
     * Getter for this Player's inventory
     * @return the current PlayerGameState of this Player.
     */
    public PlayerInventory getInventory() {
        return this.gameState.getInventory();
    }

    /**
     * Getter for the Strategy of this Player
     * @return the Strategy of this Player
     */
    public Strategy getStrategy() {
        return this.strategy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return playerId == player.playerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }
}

