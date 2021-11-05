package Common;

import Other.Connection;
import Other.ConnectionColor;
import Other.Destination;
import Other.PlayerInventory;
import Other.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


/**
 * Representation of a player's state of a Trains game.
 *
 * (Will be created by RefereeGame State)
 */
public final class PlayerGameState {
  private final TrainsMap map;
  private final List<Integer> orderOfPlayerTurns;
  private final int currentPlayer;
  private final Map<Connection, Integer> connectionsStatus;

  private final PlayerInventory inventory;

  /**
   * Constructor for a PlayerGameState, assuming thye are only created by a RefereeGameState
   * @param map the TrainsMap of the current game
   * @param orderOfPlayerTurns the order of the turns for the players (stored as the players' ids)
   * @param currentPlayer the id of the currently active player
   * @param inventory the private information of this player
   * @param connectionsStatus the statuses of all the connections in the TrainsMap
   *                          value -1: the connection is not acquired
   *                          value =! -1: the id of the player who has acquired the connection
   */
  public PlayerGameState(TrainsMap map, List<Integer> orderOfPlayerTurns, int currentPlayer,
                         Map<Connection, Integer> connectionsStatus, PlayerInventory inventory) {
    Objects.requireNonNull(map);
    Objects.requireNonNull(connectionsStatus);
    Objects.requireNonNull(inventory);
    this.map = map;
    this.orderOfPlayerTurns = setOrderOfPlayerTurns(orderOfPlayerTurns);
    this.currentPlayer = setCurrentPlayer(currentPlayer);
    this.connectionsStatus = connectionsStatus;
    this.inventory = inventory;
  }

  /**
   * Setter for the order of player turns. Validates that it is not null, contains no duplicate
   * player ids, and that none of the ids are -1
   * @param orderOfPlayerTurns the order of player turns stored as player ids
   */
  private List<Integer> setOrderOfPlayerTurns(List<Integer> orderOfPlayerTurns) {
    Objects.requireNonNull(orderOfPlayerTurns);
    Set<Integer> nonDuplicateTurns = new HashSet<>(orderOfPlayerTurns);
    if (nonDuplicateTurns.size() != orderOfPlayerTurns.size()) {
      throw new IllegalArgumentException("Order of player turns contains duplicate player ids");
    }
    if (orderOfPlayerTurns.contains(Utils.NOT_ACQUIRED_CONNECTION_STATUS)) {
      throw new IllegalArgumentException("Order of player turns cannot contain "
              + Utils.NOT_ACQUIRED_CONNECTION_STATUS);
    }
    return orderOfPlayerTurns;
  }

  /**
   * Setter for the current player. Validates that the current player id is not the same as the
   * unacquired connection status and that it exists in the order of player turns
   * @param currentPlayer the id of the current player
   */
  private int setCurrentPlayer(int currentPlayer) {
    if (currentPlayer == Utils.NOT_ACQUIRED_CONNECTION_STATUS) {
      throw new IllegalArgumentException("Current player id cannot be "
              + Utils.NOT_ACQUIRED_CONNECTION_STATUS);
    }
    if (!this.orderOfPlayerTurns.contains(currentPlayer)) {
      throw new IllegalArgumentException("Current player must exist in the order of player turns");
    }
    return currentPlayer;
  }

  /**
   * Getter for the map
   * @return TrainsMap of the game
   */
  public TrainsMap getMap() {
    return map;
  }

  /**
   * Getter for the current statuses of the connections in the game
   * @return the Map of Connection to Integer representing the connections' statuses
   */
  public Map<Connection, Integer> getConnectionsStatus() {
    return new HashMap<>(connectionsStatus);
  }

  /**
   * Getter for the cards that this player game state has in hand
   * @return the Map of ConnectionColor to Integer representing the player's hand
   */
  public Map<ConnectionColor, Integer> getCardsInHand() {
    return new HashMap<>(inventory.getCardsInHand());
  }

  /**
   * Getter for the PlayerInventory this player game state has
   * @return the PlayerInventory for the game state
   */
  public PlayerInventory getInventory() { return this.inventory; }

  /**
   * Returns the total number of cards that this player currently has in their hand
   * @return integer representing the number of cards this player has
   */
  public int getTotalNumCards() {
    int totalCards = 0;
    for (int coloredCards : this.inventory.getCardsInHand().values()) {
      totalCards += coloredCards;
    }
    return totalCards;
  }

  /**
   * Getter for the number of rails that this player game state has
   * @return the integer representing the number of rails this player game state has
   */
  public int getRailsInHand() {
    return this.inventory.getRailsInHand();
  }

  /**
   * Getter for the destinations of this player game state
   * @return the set of Destination that represents the player's destination cards
   */
  public Set<Destination> getDestinations() {
    return this.inventory.getDestinations();
  }

  /**
   * Getter for the order of player turns.
   * @return the List of Player id's in the correct order for the game
   */
  public List<Integer> getOrderOfPlayerTurns() {
    return new ArrayList<>(this.orderOfPlayerTurns);
  }

  /**
   * Getter for the current player.
   * @return return the id of the current player whose turn it is.
   */
  public int getCurrentPlayer() {
    return this.currentPlayer;
  }

  /**
   * Returns all the connections that this player could currently acquire legally.
   * @return the Set of Connections that are unacquired and legal for this player to acquire.
   */
  public Set<Connection> findAllAquirableConnections() {
    Set<Connection> acquirableConnections = new HashSet<>();
    for (Connection unacquired : this.findUnaquiredConnections()) {
      if (this.isLegalAcquisition(unacquired)) {
        acquirableConnections.add(unacquired);
      }
    }
    return acquirableConnections;
  }

  /**
   * Returns the connections in the game that have not yet been acquired by a player
   * @return the set of Connections that have not been acquired
   */
  public Set<Connection> findUnaquiredConnections() {
    Set<Connection> unacquiredConnections = new HashSet<>();
    for (Connection connection : connectionsStatus.keySet()) {
      if (connectionsStatus.get(connection) == Utils.NOT_ACQUIRED_CONNECTION_STATUS) {
        unacquiredConnections.add(connection);
      }
    }
    return unacquiredConnections;
  }

  /**
   * Determines if the given connection can be acquired by this player
   * @param connection the connection to see if it can be acquired by this player
   * @return true if the connection can be acquired. False otherwise
   */
  public boolean isLegalAcquisition(Connection connection) {
    Objects.requireNonNull(connection);
    return isConnectionUnacquired(connection) && hasEnoughCardsForAcquisition(connection)
            && hasEnoughRailsForAcquisition(connection);
  }

  /**
   * Determines if the given connection is not yet acquired by any player
   * @param connection the connection whose status to check
   * @return whether the connection is unaquired
   */
  private boolean isConnectionUnacquired(Connection connection) {
    return connectionsStatus.get(connection) == Utils.NOT_ACQUIRED_CONNECTION_STATUS;
  }

  /**
   * Determines if this player has enough of the required colored cards to acquire
   * the given connection
   * @param connection the connection that the player is asking to acquire
   * @return whether the player has enough of the specific colored cards (according
   * to the length and color of the connection) to acquire the connection
   */
  private boolean hasEnoughCardsForAcquisition(Connection connection) {
    return this.inventory.getCardsInHand().get(connection.getConnectionColor()) >= connection.getLength();
  }

  /**
   * Determines if this player has enough rails to acquire the given connection
   * @param connection the connection that this player is asking to acquire
   * @return whether this player has enough rails to acquire the connection
   */
  private boolean hasEnoughRailsForAcquisition(Connection connection) {
    return connection.getLength() <= this.inventory.getRailsInHand();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PlayerGameState that = (PlayerGameState) o;
    return this.map.equals(that.map)
            && this.connectionsStatus.equals(that.connectionsStatus)
            && this.inventory.equals(that.inventory);
  }

  @Override
  public int hashCode() {
    return Objects.hash(map, connectionsStatus, this.inventory);
  }
}
