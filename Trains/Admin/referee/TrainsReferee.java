package referee;

import com.google.common.collect.Iterables;
import game_state.RailCard;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import map.Destination;
import map.ITrainMap;
import player.IPlayer;
import referee.GameEndReport.PlayerScore;
import strategy.TurnAction;

/**
 * This Referee runs games of Trains on a given map and list of players, constructed through the
 * {@link RefereeBuilder}.
 *
 * <p>One referee object is intended to run one game of Trains.
 *
 * <p>This referee will remove players from the game for the following abnormal interactions"
 *
 * <ul>
 *   <li>Any exception thrown by the player when a method is called on it.
 *   <li>Any response from the player that is well-formed but invalid (e.g., incorrect number of
 *       destinations, attempting to acquire a connection that is already occupied)
 * </ul>
 *
 * <p>Other abnormal interactions are not directly handled by the referee and will instead be
 * handled during the project phase that adds remote communication. That being said, these
 * interactions will be handled by the referee by catching an exception thrown by the communication
 * component for the abnormal interaction so as to unilaterally avoid bringing down the system for
 * any reason.
 *
 * <p>For example:
 *
 * <ul>
 *   <li>A TimeOutException thrown by the component communicating with the player
 *   <li>An exception thrown for receiving a non-well-formed message as response from the player
 * </ul>
 */
public class TrainsReferee implements IReferee {

  private static final int PLAYER_NUM_RAILS_START = 45;
  private static final int PLAYER_NUM_CARDS_START = 4;
  private static final int PLAYER_NUM_DEST_OPTIONS = 5;
  private static final int PLAYER_NUM_DEST_TO_CHOOSE = 2;
  private static final int PLAYER_NUM_RAILS_GAME_OVER = 2;
  private static final int PLAYER_NUM_CARDS_PER_DRAW = 2;

  private final ITrainMap map;
  private final List<IPlayer> playersInOrder;
  private final Function<ITrainMap, List<Destination>> destinationProvider;
  private final Supplier<List<RailCard>> deckSupplier;
  private final Set<Integer> removedPlayersIndices;

  private TrainsReferee(
      ITrainMap map,
      List<IPlayer> playersInOrder,
      Function<ITrainMap, List<Destination>> destinationProvider,
      Supplier<List<RailCard>> deckSupplier) {
    this.map = map;
    this.playersInOrder = new ArrayList<>(playersInOrder);
    this.destinationProvider = destinationProvider;
    this.deckSupplier = deckSupplier;
    this.removedPlayersIndices = new HashSet<>();
  }

  /**
   * To construct instances of this referee, requiring a map and initial number of players, and
   * optionally a means of ordering potential destinations and creating a deck.
   */
  public static class RefereeBuilder {

    private static final int NUM_CARDS_IN_DECK = 250;

    private final ITrainMap map;
    private final List<IPlayer> playersInOrder;
    private Function<ITrainMap, List<Destination>> destinationProvider;
    private Supplier<List<RailCard>> deckProvider;

    /**
     * Constructs this builder from the required map and players.
     *
     * @param map the map for the game.
     * @param playersInOrder the players in their turn order.
     */
    public RefereeBuilder(ITrainMap map, List<IPlayer> playersInOrder) {
      this.map = map;
      this.playersInOrder = playersInOrder;
      this.destinationProvider = RefereeBuilder::defaultDestinationProvider;
      this.deckProvider = RefereeBuilder::defaultDeckSupplier;
    }

    private static List<Destination> defaultDestinationProvider(ITrainMap map) {
      List<Destination> result =
          map.getAllPossibleDestinations().stream()
              .map((pair) -> new Destination(pair))
              .collect(Collectors.toList());
      Collections.shuffle(result);
      return result;
    }

    private static List<RailCard> defaultDeckSupplier() {
      List<RailCard> result = new ArrayList<>();
      Random cardSelector = new Random();
      RailCard[] railCardOptions = RailCard.values();
      for (int cardNumber = 0; cardNumber < NUM_CARDS_IN_DECK; cardNumber += 1) {
        result.add(railCardOptions[cardSelector.nextInt(railCardOptions.length)]);
      }
      return result;
    }

    /**
     * Updates the destination provider.
     *
     * @param destinationProvider new destination provider.
     * @return the updated builder for chaining.
     */
    public RefereeBuilder destinationProvider(
        Function<ITrainMap, List<Destination>> destinationProvider) {
      this.destinationProvider = destinationProvider;
      return this;
    }

    /**
     * Updates teh deck provider.
     *
     * @param deckProvider new deck provider.
     * @return the updated builder for chaining.
     */
    public RefereeBuilder deckProvider(Supplier<List<RailCard>> deckProvider) {
      this.deckProvider = deckProvider;
      return this;
    }

    /**
     * Builds the referee, throwing exceptions if any inputs are null.
     *
     * @return the constructed referee, ready to play a game.
     */
    public TrainsReferee build() {
      Objects.requireNonNull(this.map);
      Objects.requireNonNull(this.deckProvider);
      Objects.requireNonNull(this.destinationProvider);
      Objects.requireNonNull(this.playersInOrder);
      for (IPlayer player : this.playersInOrder) {
        Objects.requireNonNull(player);
      }
      return new TrainsReferee(
          this.map, this.playersInOrder, this.destinationProvider, this.deckProvider);
    }
  }

  @Override
  public GameEndReport playGame() {
    // TODO: Validate number of players, abstract over destinations and cards
    IRefereeGameState refereeGameState = this.initializeGame();
    return this.runGame(refereeGameState);
  }

  private GameEndReport runGame(IRefereeGameState refereeGameState) {
    int numConsecutiveInsignificantTurns = 0;
    List<IPlayer> remainingPlayers = this.remainingPlayersInOrder();
    Iterator<IPlayer> turnOrder = Iterables.cycle(remainingPlayers).iterator();

    while (!this.isGameOver(numConsecutiveInsignificantTurns, refereeGameState)) {
      boolean significantTurn = this.takePlayerTurn(turnOrder, refereeGameState);

      numConsecutiveInsignificantTurns = significantTurn ? 0 : numConsecutiveInsignificantTurns + 1;
    }
    return this.calculateGameEndReport(refereeGameState);
  }

  private GameEndReport calculateGameEndReport(IRefereeGameState gameState) {
    List<Integer> finalScoresInTurnOrder = gameState.calculatePlayerScores();
    List<PlayerScore> gameReportScores = new ArrayList<>();
    for (int index = 0; index < this.playersInOrder.size(); index += 1) {
      if (!this.removedPlayersIndices.contains(index)) {
        gameReportScores.add(new PlayerScore(index, finalScoresInTurnOrder.remove(0)));
      }
    }
    gameReportScores.sort(Comparator.comparingInt(s -> s.score));
    return new GameEndReport(gameReportScores, new HashSet<>(this.removedPlayersIndices));
  }

  private List<IPlayer> remainingPlayersInOrder() {
    List<IPlayer> remainingPlayers = new ArrayList<>(this.playersInOrder);
    remainingPlayers.removeAll(
        this.removedPlayersIndices.stream()
            .map(this.playersInOrder::get)
            .collect(Collectors.toList()));
    return remainingPlayers;
  }

  /** @return boolean indicating whether turn was significant */
  private boolean takePlayerTurn(Iterator<IPlayer> turnOrder, IRefereeGameState gameState) {
    IPlayer activePlayer = turnOrder.next();

    try {
      TurnAction turn = activePlayer.takeTurn(gameState.getActivePlayerState());
      TurnResult turnApplyResult = applyActionToActivePlayer(turn, activePlayer, gameState);
      if (turnApplyResult != TurnResult.INVALID) {
        gameState.advanceTurn();
        return turnApplyResult == TurnResult.SIGNIFICANT;
      }
    } catch (Exception ignored) {
      // Any exception caught has the same effect as an invalid action: the player is removed
    }
    // TODO: Find a better way to determine index of active player - consider custom cyclic iterator
    // if decide to use indices
    this.removedPlayersIndices.add(this.playersInOrder.indexOf(activePlayer));
    turnOrder.remove();
    gameState.removeActivePlayer();
    return true;
  }

  private TurnResult applyActionToActivePlayer(
      TurnAction action, IPlayer player, IRefereeGameState gameState) {
    switch (action.getActionType()) {
      case DRAW_CARDS:
        List<RailCard> drawnCards = gameState.drawCardsForActivePlayer(PLAYER_NUM_CARDS_PER_DRAW);
        player.receiveCards(new ArrayList<>(drawnCards));
        return drawnCards.isEmpty() ? TurnResult.INSIGNIFICANT : TurnResult.SIGNIFICANT;
      case ACQUIRE_CONNECTION:
        boolean connectionAcquired =
            gameState.acquireConnectionForActivePlayer(action.getRailConnection().get());
        return connectionAcquired ? TurnResult.SIGNIFICANT : TurnResult.INVALID;
      default:
        return TurnResult.INVALID;
    }
  }

  private enum TurnResult {
    SIGNIFICANT,
    INSIGNIFICANT,
    INVALID
  }

  private boolean isGameOver(int numConsecutiveInsignificantTurns, IRefereeGameState gameState) {
    return numConsecutiveInsignificantTurns == this.numPlayersRemaining()
        || gameState.getActivePlayerState().getNumRails() <= PLAYER_NUM_RAILS_GAME_OVER;
  }

  private int numPlayersRemaining() {
    return this.playersInOrder.size() - this.removedPlayersIndices.size();
  }

  // region Initialization

  private IRefereeGameState initializeGame() {
    List<Destination> activeDestinationList =
        new ArrayList<>(this.destinationProvider.apply(this.map));
    List<RailCard> deck = this.deckSupplier.get();
    List<IPlayerData> playerDataInTurnOrder = new ArrayList<>();
    for (int ii = 0; ii < playersInOrder.size(); ii++) {
      Optional<IPlayerData> thisPlayersData =
          this.setupPlayer(this.playersInOrder.get(ii), activeDestinationList, deck, map);
      if (thisPlayersData.isPresent()) {
        playerDataInTurnOrder.add(thisPlayersData.get());
      } else {
        this.removedPlayersIndices.add(ii);
      }
    }

    return new RefereeGameState(playerDataInTurnOrder, deck, map);
  }

  private Optional<IPlayerData> setupPlayer(
      IPlayer player,
      List<Destination> activeDestinationList,
      List<RailCard> activeDeck,
      ITrainMap map) {

    // determine setup info
    List<RailCard> playerStartingHand = activeDeck.subList(0, PLAYER_NUM_CARDS_START);
    IPlayerHand<RailCard> startingHand = new TrainsPlayerHand(playerStartingHand);

    // choosing destinations
    List<Destination> playerDestinationOptions =
        activeDestinationList.subList(0, PLAYER_NUM_DEST_OPTIONS);

    Set<Destination> chosenDestinations = new HashSet<>();
    try {
      player.setup(map, PLAYER_NUM_RAILS_START, new ArrayList<>(playerStartingHand));
      chosenDestinations = player.chooseDestinations(new HashSet<>(playerDestinationOptions));
    } catch (Exception e) {
      return Optional.empty();
    }

    if (!validDestinationChoice(new HashSet<>(playerDestinationOptions), chosenDestinations)) {
      return Optional.empty();
    }
    // TODO: Figure out playerID here
    IPlayerData result =
        new PlayerData(
            "REMOVE THIS",
            startingHand,
            PLAYER_NUM_RAILS_START,
            chosenDestinations,
            new HashSet<>());
    playerStartingHand.clear(); // once drawn, they're gone
    activeDestinationList.removeAll(chosenDestinations);

    return Optional.of(result);
  }

  private boolean validDestinationChoice(Set<Destination> options, Set<Destination> chosen) {
    return options.containsAll(chosen) && chosen.size() == PLAYER_NUM_DEST_TO_CHOOSE;
  }
  // endregion
}
