package Admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import Common.PlayerGameState;
import Common.TrainsMap;
import Other.*;

/**
 * Representation of a referee's state of a Trains game
 */
public class RefereeGameState {
  private final TrainsMap map;
  private List<Integer> orderOfPlayerTurns;
  private int currentPlayer;
  private Map<Connection, Integer> connectionsStatus;

  private Map<Integer, PlayerInventory> inventoryPerPlayer;
  private GamePhase phase = GamePhase.start;

  /**
   * Constructor for a RefereeGameState
   * @param map the TrainsMap of the current game
   * @param orderOfPlayerTurns the order of the turns for the players (stored as the players' ids)
   * @param currentPlayer the id of the currently active player
   * @param connectionsStatus the statuses of all the connections in the TrainsMap
   *                          value -1: the connection is not acquired
   *                          value =! -1: the id of the player who has acquired the connection
   * @param railsPerPlayer the number of rails each player currently has
   * @param cardsPerPlayer the number of each of color of card in each player's hand
   * @param destinationsPerPlayer the destinations of each player
   */
  public RefereeGameState(TrainsMap map, List<Integer> orderOfPlayerTurns, int currentPlayer,
                          Map<Connection, Integer> connectionsStatus,
                          Map<Integer, Integer> railsPerPlayer,
                          Map<Integer, Map<ConnectionColor, Integer>> cardsPerPlayer,
                          Map<Integer, Set<Destination>> destinationsPerPlayer) {
    Objects.requireNonNull(map);
    this.map = map;
    this.setOrderOfPlayerTurns(orderOfPlayerTurns);
    this.setCurrentPlayer(currentPlayer);
    this.setConnectionsStatus(connectionsStatus);
    this.setPlayerInventories(railsPerPlayer, cardsPerPlayer, destinationsPerPlayer);
  }

  /**
   * Constructor for a RefereeGameState
   * @param map the TrainsMap of the current game
   * @param orderOfPlayerTurns the order of the turns for the players (stored as the players' ids)
   * @param currentPlayer the id of the currently active player
   * @param connectionsStatus the statuses of all the connections in the TrainsMap
   *                          value -1: the connection is not acquired
   *                          value =! -1: the id of the player who has acquired the connection
   */
  public RefereeGameState(TrainsMap map, List<Integer> orderOfPlayerTurns, int currentPlayer,
                          Map<Connection, Integer> connectionsStatus) {
    Objects.requireNonNull(map);
    this.map = map;

    this.setOrderOfPlayerTurns(orderOfPlayerTurns);
    this.setCurrentPlayer(currentPlayer);
    this.setConnectionsStatus(connectionsStatus);
    this.inventoryPerPlayer = new HashMap<>();
  }

  /**
   * Setter for the order of player turns. Validates that it is not null, contains no duplicate
   * player ids, and that none of the ids are -1
   * @param orderOfPlayerTurns the order of player turns stored as player ids
   */
  public void setOrderOfPlayerTurns(List<Integer> orderOfPlayerTurns) {
    Objects.requireNonNull(orderOfPlayerTurns);
    Set<Integer> nonDuplicateTurns = new HashSet<>(orderOfPlayerTurns);
    if (nonDuplicateTurns.size() != orderOfPlayerTurns.size()) {
      throw new IllegalArgumentException("Order of player turns contains duplicate player ids");
    }
    if (orderOfPlayerTurns.contains(Utils.NOT_ACQUIRED_CONNECTION_STATUS)) {
      throw new IllegalArgumentException("Order of player turns cannot contain "
              + Utils.NOT_ACQUIRED_CONNECTION_STATUS);
    }
    this.orderOfPlayerTurns = orderOfPlayerTurns;
  }

  /**
   * Setter for the current player. Validates that the current player id is not the same as the
   * unacquired connection status and that it exists in the order of player turns
   * @param currentPlayer the id of the current player
   */
  public void setCurrentPlayer(int currentPlayer) {
    if (currentPlayer == Utils.NOT_ACQUIRED_CONNECTION_STATUS) {
      throw new IllegalArgumentException("Current player id cannot be "
              + Utils.NOT_ACQUIRED_CONNECTION_STATUS);
    }
    if (!this.orderOfPlayerTurns.contains(currentPlayer)) {
      throw new IllegalArgumentException("Current player must exist in the order of player turns");
    }
    this.currentPlayer = currentPlayer;
  }

  /**
   * Setter for the connectionsStatus. Validates it isn't null, contains the same connections
   * as the connections in the map, and that the statuses for each connection are either a player
   * id or the integer that indicates a connection is not acquired
   * @param connectionsStatus the statuses of each connection in this game
   */
  public void setConnectionsStatus(Map<Connection, Integer> connectionsStatus) {
    Objects.requireNonNull(connectionsStatus);
    if (!map.getConnections().equals(connectionsStatus.keySet())) {
      throw new IllegalArgumentException("Connections in the connection statuses must be the " +
              "same connections as the map connections");
    }
    for (int status : connectionsStatus.values()) {
      if (!this.orderOfPlayerTurns.contains(status) && status != Utils.NOT_ACQUIRED_CONNECTION_STATUS) {
        throw new IllegalArgumentException("Connection status must be the id of a player in this " +
                "game or " + Utils.NOT_ACQUIRED_CONNECTION_STATUS);
      }
    }
    this.connectionsStatus = connectionsStatus;
  }

  /**
   * Validates that the player ids (keys) all exist in the order of player turns and that none of them are
   * the status of an unacquired connection. Also ensures no rails counts are negative.
   * @param railsPerPlayer the number of rails per player
   * @return the rails for the player
   */
  public Map<Integer, Integer> validateRailsPerPlayer(Map<Integer, Integer> railsPerPlayer) {
    Objects.requireNonNull(railsPerPlayer);
    validatePlayersIDsInDictAreValid(railsPerPlayer);
    for (Integer numRails : railsPerPlayer.values()) {
      if (numRails < 0) {
        throw new IllegalArgumentException("Player cannot have a negative number of rails");
      }
    }
    return railsPerPlayer;
  }

  /**
   * Validates the player ids are all valid, that each player's hand of cards contain all colored cards
   * colors, and no players have a negative amount of cards.
   * @param cardsPerPlayer the number of colored cards (per color) per player
   * @return the cards per player
   */
  private Map<Integer, Map<ConnectionColor, Integer>> validateCardsPerPlayer(Map<Integer, Map<ConnectionColor, Integer>> cardsPerPlayer) {
    Objects.requireNonNull(cardsPerPlayer);
    validatePlayersIDsInDictAreValid(cardsPerPlayer);

    for (Map<ConnectionColor, Integer> cards : cardsPerPlayer.values()) {
      if (!new HashSet<>(Arrays.asList(ConnectionColor.values())).equals(cards.keySet())) {
        throw new IllegalArgumentException("Players' card hands must contain all possible " +
                "colors of colored cards");
      }
      for (Integer numCards : cards.values()) {
        if (numCards < 0) {
          throw new IllegalArgumentException("Cannot have a negative number of colored cards " +
                  "for a player");
        }
      }
    }
    return cardsPerPlayer;
  }

  /**
   * Validates that the player ids (keys) all exist in the order of player turns and that none of them are the status o
   * of an unacquired connection. Also ensures each player has the correct number of destinations.
   * @param destinationsPerPlayer the destinations per player
   * @return the destinations per each player
   */
  private Map<Integer, Set<Destination>> validateDestinationsPerPlayer(Map<Integer, Set<Destination>> destinationsPerPlayer) {
    Objects.requireNonNull(destinationsPerPlayer);
    validatePlayersIDsInDictAreValid(destinationsPerPlayer);
    for (Set<Destination> destinations : destinationsPerPlayer.values()) {
      if (destinations.size() != Utils.DESTINATIONS_PER_PLAYER) {
        throw new IllegalArgumentException("Player can only have "
                + Utils.DESTINATIONS_PER_PLAYER + "destinations");
      }
    }
    return destinationsPerPlayer;
  }

  /**
   * Setter for PlayerInventory.
   * @param railsPerPlayer map of rails for every player
   * @param cardsPerPlayer map of cards for every player
   * @param destinationsPerPlayer map of destinations for every player
   */
  public void setPlayerInventories(Map<Integer, Integer> railsPerPlayer, Map<Integer, Map<ConnectionColor, Integer>> cardsPerPlayer, Map<Integer, Set<Destination>> destinationsPerPlayer) {
      Objects.requireNonNull(railsPerPlayer);
      Objects.requireNonNull(cardsPerPlayer);
      Objects.requireNonNull(destinationsPerPlayer);

      Map<Integer, PlayerInventory> inventories = new HashMap<>();
      Map<Integer, Integer> rails = validateRailsPerPlayer(railsPerPlayer);
      Map<Integer, Map<ConnectionColor, Integer>> cards = validateCardsPerPlayer(cardsPerPlayer);
      Map<Integer, Set<Destination>> destinations = validateDestinationsPerPlayer(destinationsPerPlayer);

      for (Integer playerId : railsPerPlayer.keySet()) {
        PlayerInventory inventory = new PlayerInventory(cards.get(playerId),
                rails.get(playerId), destinations.get(playerId));
        inventories.put(playerId, inventory);
      }
      this.inventoryPerPlayer = inventories;
  }

  /**
   * Updates the PlayerInventory given a player id
   */
  public void setPlayerInventory(int id, PlayerInventory inventory) {
    this.inventoryPerPlayer.put(id, inventory);
  }

  /**
   * Validates that all the player ids within the given hashmap are valid
   * (they are not the same as the unacquired connection status and all player ids are the
   * same ids in the orderOfPlayerTurns)
   * @param playerIdsWithSomething the hashmap containing player ids as keys
   * @param <T> the values within the hashmap that we do nothing with
   */
  private <T> void validatePlayersIDsInDictAreValid(Map<Integer, T> playerIdsWithSomething) {
    if (playerIdsWithSomething.containsKey(Utils.NOT_ACQUIRED_CONNECTION_STATUS)) {
      throw new IllegalArgumentException("Player id cannot be " + Utils.NOT_ACQUIRED_CONNECTION_STATUS);
    }
    if (!playerIdsWithSomething.keySet().equals(new HashSet<>(this.orderOfPlayerTurns))) {
      throw new IllegalArgumentException("Player ids must be the same as the order of " +
              "player turns ids");
    }
  }

  /**
   * Sets the phase of the game for this RefereeGameState
   * @param phase one of GamePhase (start, play, last, end)
   */
  public void updateGamePhase(GamePhase phase) {
    this.phase = phase;
  }

  /**
   * Updates the game state with a new connection
   * @param connection the connection that the given player id has acquired
   * @param id int id of the player
   */
  public void updateConnectionStatus(Connection connection, int id) {
    if (connectionsStatus.get(connection) != Utils.NOT_ACQUIRED_CONNECTION_STATUS) {
      throw new IllegalCallerException("Illegal Acquisition"); // should never get here if we are checking correctly
    }
    this.connectionsStatus.put(connection, id);
  }

  /**
   * Updates the inventory for the given player id
   * @param inventory the PlayerInventory
   * @param id the integer id of a player
   */
  public void updatePlayerInventory(PlayerInventory inventory, int id) {
    this.inventoryPerPlayer.put(id, inventory);
  }

  /**
   * Getter for the Trains map
   * @return the Trains map
   */
  public TrainsMap getMap() {
    return map;
  }

  /**
   * Getter for the order of player turns
   * @return the ids of the player in order of their turns
   */
  public List<Integer> getOrderOfPlayerTurns() {
    return new ArrayList<>(orderOfPlayerTurns);
  }

  /**
   * Getter for the current player
   * @return the id of the current player
   */
  public int getCurrentPlayer() {
    return currentPlayer;
  }

  /**
   * Getter for the statuses of each Connection in the map
   * @return the statuses of each Connection in the map
   */
  public Map<Connection, Integer> getConnectionsStatus() {
    return new HashMap<>(connectionsStatus);
  }

  /**
   * Getter for the number of rails for the current player
   * @return the number of rails the current player has
   */
  public Integer getRailsForCurrentPlayer() {
    return this.inventoryPerPlayer.get(currentPlayer).getRailsInHand();
  }

  /**
   * Determines if the last round of turns should begin
   * @return true if the last round should begin (i.e. a player has less than 2 rails)
   */
  public boolean beginLastRound() {
    for (int id : orderOfPlayerTurns) {
      if (this.inventoryPerPlayer.get(id).getRailsInHand() <= 2) {
        return true;
      }
    }
    return false;
  }


  /**
   * Getter for the number of rails for a given player
   * @return the number of rails the given player currently has
   */
  public Integer getRailsForGivenPlayer(int id) {
    return this.inventoryPerPlayer.get(id).getRailsInHand();
  }

  /**
   * Getter for the cards for the current player
   * @return the number of cards (for specified colors) that the current player  has
   */
  public Map<ConnectionColor, Integer> getCardsForCurrentPlayer() {
    return this.inventoryPerPlayer.get(currentPlayer).getCardsInHand();
  }

  /**
   * Getter for the cards for a given player.
   * @return the number of cards (per specified colors) that the given player currently has
   */
  public Map<ConnectionColor, Integer> getCardsForGivenPlayer(int id) {
    return this.inventoryPerPlayer.get(id).getCardsInHand();
  }

  /**
   * Getter for the destinations for the current player
   * @return the destinations that the current player has
   */
  public Set<Destination> getDestinationsForCurrentPlayer() {
    return this.inventoryPerPlayer.get(currentPlayer).getDestinations();
  }

  /**
   * Getter for the destinations for the given player
   * @return the destinations that the given player currently has
   */
  public Set<Destination> getDestinationsForGivenPlayer(int id) {
    return this.inventoryPerPlayer.get(id).getDestinations();
  }

  /**
   * Getter for the state of the game
   * @return a GameState
   */
  public GamePhase getGamePhase() {
    return this.phase;
  }

  /**
   * Getter for the inventories of each player id
   * @return the PlayerInventory for every player id
   */
  public Map<Integer, PlayerInventory> getInventories() {
    return new HashMap<>(this.inventoryPerPlayer);
  }

  /**
   * Removes the given player's inventory based on their playerID
   * @param playerID the id of the player whose inventory we want to remove
   */
  public void removePlayerInventory(int playerID) {
    this.inventoryPerPlayer.remove(playerID);
  }

  /**
   * Removes the given player from the order of turns
   * @param playerID the id of the player who we want to remove from the order of turns
   */
  public void removePlayerFromTurns(int playerID) {
    if (currentPlayer == playerID) {
      for (int i = 0; i < orderOfPlayerTurns.size(); i++) {
        if (orderOfPlayerTurns.get(i) == playerID) {
          this.currentPlayer = orderOfPlayerTurns.get((i + 1) % orderOfPlayerTurns.size());
        }
      }
    }
    this.orderOfPlayerTurns.remove(orderOfPlayerTurns.indexOf(playerID));
  }

  /**
   * Determines if the given connection can be acquired by the player whose turn it is
   * @param connection the connection to see if it can be acquired by the player
   * @return whether the connection can be acquired by the current player
   */
  public boolean isLegalAcquisition(Connection connection) {
    Objects.requireNonNull(connection);
    return isConnectionUnacquired(connection) && hasEnoughCardsForAcquisition(connection)
            && hasEnoughRailsForAcquisition(connection);
  }

  /**
   * Determines if the given connection is not yet acquired by a player
   * @param connection the connection whose status to check
   * @return whether the connection is unaquired
   */
  private boolean isConnectionUnacquired(Connection connection) {
    return connectionsStatus.get(connection) == Utils.NOT_ACQUIRED_CONNECTION_STATUS;
  }

  /**
   * Determines if the current player has enough of the required colored cards to acquire
   * the given connection
   * @param connection the connection that the current player is asking to acquire
   * @return whether the current player has enough of the specific colored cards (according
   * to the length and color of the connection) to acquire the connection
   */
  private boolean hasEnoughCardsForAcquisition(Connection connection) {
    int connectionLength = connection.getLength();
    ConnectionColor connectionColor = connection.getConnectionColor();
    Map<ConnectionColor, Integer> currentPlayerCards = getCardsForCurrentPlayer();
    int playerConnectionColorCards = currentPlayerCards.get(connectionColor);
    return connectionLength <= playerConnectionColorCards;
  }

  /**
   * Determines if the current player has enough rails to acquire the given connection
   * @param connection the connection that the current player is asking to acquire
   * @return whether the current player has enough rails to acquire the connection
   */
  private boolean hasEnoughRailsForAcquisition(Connection connection) {
    int connectionLength = connection.getLength();
    int currentPlayerRails = getRailsForCurrentPlayer();
    return connectionLength <= currentPlayerRails;
  }

  /**
   * Returns a new PlayerGameState representing the knowledge of the currently active player
   * @return the game state of the currently active player
   */
  public PlayerGameState createCurrentPlayerGameState() {
    PlayerInventory currentPlayerInventory = new PlayerInventory(getCardsForCurrentPlayer(), getRailsForCurrentPlayer(),
            getDestinationsForCurrentPlayer());

    return new PlayerGameState(map, orderOfPlayerTurns, currentPlayer, connectionsStatus, currentPlayerInventory);
  }
}
