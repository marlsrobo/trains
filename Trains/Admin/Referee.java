package Admin;

import Common.PlayerGameState;
import Common.TrainsMap;
import Other.*;
import Other.Action.Action;
import Other.Action.ActionEnum;
import Player.Player;

import java.util.*;

/**
 * Class representing a Referee in a game of Trains. Runs the complete game and handles kicking players out.
 */
public class Referee {
    private static final int NUM_DESTINATIONS_OFFER = 5;
    private final static int RAILS_PER_PLAYER = 45;
    private static final int INIT_CARDS_PER_PLAYER = 4;
    private static final int MORE_CARDS_TO_GIVE = 2;
    private static final int LONGEST_PATH_POINTS = 20;
    private static final int DESTINATION_REACHED_POINTS = 10;

    private List<ConnectionColor> deck = initializeDeck();
    private Set<Destination> destinations;

    private RefereeGameState gameState;
    private List<Player> activePlayers;
    private List<Player> eliminatedPlayers = new ArrayList<>();

    /**
     * Constructor for a Referee at the very beginning of a game, before having set up anything.
     * @param map the TrainsMap to be used for the game
     * @param activePlayers the list of Players that will be playing in the game that this Referee is controlling.
     */
    public Referee(TrainsMap map, List<Player> activePlayers) {
        Objects.requireNonNull(map);
        Objects.requireNonNull(activePlayers);
        Set<Player> nonDuplicatePlayers = new HashSet<>(activePlayers);
        if (nonDuplicatePlayers.size() != activePlayers.size()) {
            throw new IllegalArgumentException("List of players contains duplicate players");
        }
        if (nonDuplicatePlayers.size() > 8 || nonDuplicatePlayers.size() < 2) {
            throw new IllegalArgumentException("Number of players must be between 2 and 8");
        }
        this.activePlayers = activePlayers;
        this.gameState = createBeginningRefereeGameState(map);
        this.destinations = map.getFeasibleDestinations();
    }

    /**
     * Creates the initial RefereeGameState for this referee that does not yet contain any player inventories
     * since they haven't been initialized yet per player
     * @param map a TrainsMap for the game
     * @return a new RefereeGameState
     */
    private RefereeGameState createBeginningRefereeGameState(TrainsMap map) {
        return new RefereeGameState(map, initalizeOrderOfPlayerTurns(),
                initializeCurrentPlayer(), initializeConnectionsStatus(map));
    }

    /**
     * Initializes the order of player turns by their IDs
     * @return the list of player id's in the correct order
     */
    private List<Integer> initalizeOrderOfPlayerTurns() {
        List<Integer> playerTurns = new ArrayList<>();
        for (Player player : this.activePlayers) {
            playerTurns.add(player.getPlayerID());
        }
        // Assuming these are given to us in descending order of age!
        return playerTurns;
    }

    /**
     * Initializes the current player when the game first begins as the first player in the list of turns
     * @return the id of the current player
     */
    private int initializeCurrentPlayer() {
        return activePlayers.get(0).getPlayerID();
    }

    /**
     * Initializes the statuses of all the connections in the given map as unacquired
     * @param map the TrainsMap containing Connections
     * @return a Map of Connection to Integer representing the statuses of all the Connections
     */
    private Map<Connection, Integer> initializeConnectionsStatus(TrainsMap map) {
        Map<Connection, Integer> connectionStatuses = new HashMap<>();
        Set<Connection> mapConnections = map.getConnections();
        for (Connection connection : mapConnections) {
            connectionStatuses.put(connection, Utils.NOT_ACQUIRED_CONNECTION_STATUS);
        }
        return connectionStatuses;
    }

    /**
     * Initializes the deck of cards.
     * @return a List of ConnectionColor representing colored cards.
     */
    private List<ConnectionColor> initializeDeck() {
        List<ConnectionColor> deck = new ArrayList<>();
        int totalNumCards;
        for (int colorNum = 0; colorNum < ConnectionColor.values().length; colorNum++) {
            if (colorNum % 2 == 0) { totalNumCards = 62; }
            else totalNumCards = 63;
            for (int i = 0; i < totalNumCards; i++) {
                deck.add(ConnectionColor.numberToColor(colorNum));
            }

        }
        return deck;
    }

    /**
     * Plays a game of Trains with the Referee's TrainsMap and list of Players.
     */
    public void playGame() {
        while (this.gameState.getGamePhase() != GamePhase.end) {
            if (this.gameState.getGamePhase() == GamePhase.start) {
                // setup each player
                // give destinations to each player
                setUpPhase();
                updateGameState(GamePhase.play);
            }
            else if (this.gameState.getGamePhase() == GamePhase.play) {
                while (!this.gameState.beginLastRound() && this.gameState.getGamePhase() != GamePhase.end) {
                    if (noMoreMovesPossible()) {
                        updateGameState(GamePhase.end);
                    }
                    // grant turns to each player until end condition met
                    grantCurrentPlayerTurn();
                }
                if (this.gameState.getGamePhase() != GamePhase.end) {
                    updateGameState(GamePhase.lastRound);
                }
            }
            else if (this.gameState.getGamePhase() == GamePhase.lastRound) {
                // grants one last turn to each player
                lastRoundPhase();
                updateGameState(GamePhase.end);
            }
        }
        endPhase();
    }

    /**
     * Determines if there are any more moves possible by any player. A move is possible if a player
     * could acquire a connection or pick more cards.
     * @return true if there are no more moves possible, false if the game could continue
     */
    private boolean noMoreMovesPossible() {
        for (Player player : this.activePlayers) {
            if (player.getGameState().findAllAquirableConnections().size() != 0) {
                return false;
            }
        }
        boolean allConnectionsAcquired = true;
        for (Integer connectionStatus : this.gameState.getConnectionsStatus().values()) {
            if (connectionStatus == Utils.NOT_ACQUIRED_CONNECTION_STATUS) {
                allConnectionsAcquired = false;
                break;
            }
        }
        return deck.size() == 0 || allConnectionsAcquired;
    }

    /**
     * Starts the setup for a game of Trains
     */
    private void setUpPhase() {
        // set up the inventory and destinations of each Player in the order of turns
        for (Player player : activePlayers) {
            setupInventory(player);
            setupDestinations(player);
        }
        for (Player player : eliminatedPlayers) {
            eliminatePlayer(player);
        }
    }

    /**
     * Sets up the specific player's inventory with initial cards, rails, and their map
     * @param player the Player to setup their initial game pieces
     */
    private void setupInventory(Player player) {
        Map<ConnectionColor, Integer> cardsInHand = giveCards(INIT_CARDS_PER_PLAYER);
        player.setup(gameState.getMap(), RAILS_PER_PLAYER, cardsInHand);
        PlayerInventory inventory = new PlayerInventory(cardsInHand, RAILS_PER_PLAYER);
        this.gameState.setPlayerInventory(player.getPlayerID(), inventory);
    }

    /**
     * Returns a random selection of cards from the deck of size numCards
     * @param numCards the number of cards to randomly select
     * @return a Map of ConnectionColor to Integer representing the number of each colored card.
     */
    private Map<ConnectionColor, Integer> giveCards(int numCards) {
        if (deck.size() < numCards) {
            throw new ArrayIndexOutOfBoundsException("Not enough cards to give out to players.");
        }
        Map<ConnectionColor, Integer> moreCards = new HashMap<>();
        for (ConnectionColor color : ConnectionColor.values()) {
            moreCards.put(color, 0);
        }

        Collections.shuffle(deck);
        for (int i = 0; i < numCards; i++) {
            moreCards.put(deck.get(i), moreCards.get(deck.get(i)) + 1);
        }
        for (int i = 0; i < numCards; i++) {
            deck.remove(deck.get(0));
        }

        return moreCards;
    }

    /**
     * Sets up the destinations for the given player by offering it a certain amount of available
     * destinations, then appropriately setting the player's destinations according to what they chose
     * @param player the Player whose destinations are being setup
     */
    private void setupDestinations(Player player) {
        List<Destination> destinationsList = new ArrayList<>(this.destinations);
        Set<Destination> offeredDestinations = new HashSet<>();
        // pick the destinations to offer to the player to pick
        for (int i = 0; i < NUM_DESTINATIONS_OFFER; i++) {
            offeredDestinations.add(destinationsList.get(i));
        }
        // offer destinations to player and get the destinations the player didn't pick
        Set<Destination> notPickedDestinations = player.pick(offeredDestinations);

        // eliminate the player if they returned destinations that weren't part of the set offered to them
        if (!offeredDestinations.containsAll(notPickedDestinations)
                || (notPickedDestinations.size() != NUM_DESTINATIONS_OFFER - Utils.DESTINATIONS_PER_PLAYER)) {
            this.eliminatedPlayers.add(player);
            return;
        }

        // remove the destinations that the player did pick from the destinations another
        // player could pick from
        offeredDestinations.removeAll(notPickedDestinations);
        this.destinations.removeAll(offeredDestinations);

        // update the player's inventory in the referee game state with their destinations
        PlayerInventory playerInventoryNoDestinations = player.getInventory();
        PlayerInventory playerInventoryWithDestinations =
                new PlayerInventory(playerInventoryNoDestinations.getCardsInHand(),
                        playerInventoryNoDestinations.getRailsInHand(), offeredDestinations);

        this.gameState.setPlayerInventory(player.getPlayerID(), playerInventoryWithDestinations);
    }

    /**
     * Grants the current player a turn and updates the game state accordingly.
     * Also eliminates a player if they cheat, and advances the current player after the turn.
     */
    private void grantCurrentPlayerTurn() {
        Player currentPlayer = this.getCurrentPlayer();

        PlayerGameState playerGameState = this.gameState.createCurrentPlayerGameState();
        Action action = currentPlayer.play(playerGameState);

        if (action.getActionType() == ActionEnum.more_cards) {
            tryCardsAction(currentPlayer);
            this.gameState.setCurrentPlayer(getNextPlayerID(currentPlayer.getPlayerID()));
        }
        else {
            if (this.gameState.isLegalAcquisition(action.getValue())) {
                updateNewAcquisition(currentPlayer, action);
                this.gameState.setCurrentPlayer(getNextPlayerID(currentPlayer.getPlayerID()));
            }
            else {
                this.eliminatedPlayers.add(currentPlayer);
                eliminatePlayer(currentPlayer);
            }
        }
    }

    /**
     * Gives a player more cards if there are cards left in the deck. If there aren't enough cards,
     * the referee ignores the current player's request and moves on to the next player
     * @param currentPlayer the Player who is asking for more cards
     */
    private void tryCardsAction(Player currentPlayer) {
        // give player more cards + update the deck
        try {
            Map<ConnectionColor, Integer> cardsToGive = giveCards(MORE_CARDS_TO_GIVE);
            currentPlayer.more(cardsToGive);
            Map<ConnectionColor, Integer> currentPlayerCards = this.gameState.getCardsForCurrentPlayer();
            for (ConnectionColor color : cardsToGive.keySet()) {
                currentPlayerCards.put(color, cardsToGive.get(color) + currentPlayerCards.get(color));
            }
            Integer playerRails = this.gameState.getRailsForCurrentPlayer();
            Set<Destination> playerDestinations = this.gameState.getDestinationsForCurrentPlayer();
            PlayerInventory newInventory = new PlayerInventory(currentPlayerCards, playerRails, playerDestinations);
            this.gameState.setPlayerInventory(this.gameState.getCurrentPlayer(), newInventory);
        }
        // if there are no more cards left to give do nothing and skip to the next player
        catch (ArrayIndexOutOfBoundsException ignored) {
        }
    }

    /**
     * Eliminates a player from the game by removing them from the list of active players and
     * discarding all of their resources
     * @param player the Player to eliminate from the game
     */
    private void eliminatePlayer(Player player) {

        // reset the connections that this player had acquired to unacquired
        resetPlayersConnectionsToUnacquired(player);

        // remove the player's inventory from the game state
        this.gameState.removePlayerInventory(player.getPlayerID());

        // remove the player from the order of turns
        this.gameState.removePlayerFromTurns(player.getPlayerID());

        // remove player from list of current players
        this.activePlayers.remove(player);
    }

    /**
     * Resets the connections that the given player had acquired to unacquired
     * @param player the Player whose connections are being reset
     */
    private void resetPlayersConnectionsToUnacquired(Player player) {
        Set<Connection> playersConnections = getPlayerAcquiredConnections(player);
        Map<Connection, Integer> connectionsStatuses = this.gameState.getConnectionsStatus();
        for (Connection connection : playersConnections) {
            connectionsStatuses.put(connection, Utils.NOT_ACQUIRED_CONNECTION_STATUS);
        }
        this.gameState.setConnectionsStatus(connectionsStatuses);
    }

    /**
     * Returns the active Player
     * @return the active Player
     */
    private Player getCurrentPlayer() {
        for (Player player : this.activePlayers) {
            if (player.getPlayerID() == this.gameState.getCurrentPlayer()) {
                return player;
            }
        }
        throw new IllegalArgumentException("Player is not active");
    }

    /**
     * Returns the id of the Player who is next in line to take a turn
     * @param currentPlayerID the id of the current player
     * @return the id of the next player in the order of turns (after the current player)
     */
    private int getNextPlayerID(int currentPlayerID) {
        List<Integer> playerTurnsById = this.gameState.getOrderOfPlayerTurns();
        for (int i = 0; i < playerTurnsById.size(); i++) {
            if (playerTurnsById.get(i) == currentPlayerID) {
                return playerTurnsById.get((i + 1) % playerTurnsById.size());
            }
        }
        throw new IllegalArgumentException("No player exists with the given id");
    }

    /**
     * Updates the game state with information about the successful acquisition of a connection for the given player
     * @param player the Player who has acquired a connection
     * @param action the Acquired connection
     */
    private void updateNewAcquisition(Player player, Action action) {
        Connection acquired = action.getValue();
        // update connection status
        this.gameState.updateConnectionStatus(acquired, player.getPlayerID());

        Map<ConnectionColor, Integer> cards = this.gameState.getCardsForGivenPlayer(player.getPlayerID());
        cards.put(acquired.getConnectionColor(), cards.get(acquired.getConnectionColor()) - acquired.getLength());
        int rails = this.gameState.getRailsForGivenPlayer(player.getPlayerID());

        // update player inventory to remove cards/rails for the acquired.
        PlayerInventory inventory = new PlayerInventory(cards,
                (rails - acquired.getLength()),
                player.getInventory().getDestinations());
        this.gameState.updatePlayerInventory(inventory, player.getPlayerID());
    }


    /**
     * Updates the phase of the game for this Referee's game state
     * @param phase a GamePhase
     */
    private void updateGameState(GamePhase phase) {
        this.gameState.updateGamePhase(phase);
    }

    /**
     * The last round of turns that grants each remaining player a turn. Ends the game early
     * by returning if no more moves are possible.
     */
    private void lastRoundPhase() {
        for (int i = 0; i < this.gameState.getOrderOfPlayerTurns().size() - 1; i++) {
            if (noMoreMovesPossible()) {
                return;
            }
            grantCurrentPlayerTurn();
        }
    }

    /**
     * Starts the end phase for the game of Trains which consists of telling each player that
     * participated in the game whether they won or lost
     */
    private void endPhase() {
        List<Set<Player>> ranking = getRanking();
        for (Player player : activePlayers) {
            player.win(ranking.get(0).contains(player)); // only players in the first set of players are the winners
        }
        for (Player eliminated : eliminatedPlayers) {
            eliminated.win(false);
        }
    }


    /**
     * Getter for the deck
     * @return a List of connection colors representing colored cards in the deck.
     */
    public List<ConnectionColor> getDeck() {
        return new ArrayList<>(deck);
    }

    /**
     * Getter for the destinations not picked
     * @return a Set of Destination representing unpicked destinations
     */
    public Set<Destination> getDestinations() {
        return new HashSet<>(destinations);
    }

    /**
     * Getter for the referee game state
     * @return a RefereeGameState
     */
    public RefereeGameState getGameState() {
        return gameState;
    }

    /**
     * Getter for the current list of active players
     * @return a List of Player representing players that can make moves during the game.
     */
    public List<Player> getActivePlayers() {
        return new ArrayList<>(activePlayers);
    }

    /**
     * Returns all of the players that have been eliminated from the game
     * @return a List of Player representing booted players
     */
    public List<Player> getEliminatedPlayers() {
        return new ArrayList<>(this.eliminatedPlayers);
    }

    // --------------------------SCORING/RANKING--------------------------------------------------

    /**
     * Get the ranking for all the players, both active and eliminated.
     * Multiple players in the same set means they have the same score.
     * @return a sorted List of Set of Players ranked by their score.
     */
    public List<Set<Player>> getRanking() {
        Map<Integer, Set<Player>> scores = new HashMap<>();
        Set<Integer> playerIDsWithLongestPath = getIDsofPlayersWithLongestPath();

        // putting all the scores and corresponding player id's in scores hashmap
        for (Player player : activePlayers) {
            int score = calculatePlayerScoreWithoutBonus(player);
            // add bonus points if player has longest path
            if (playerIDsWithLongestPath.contains(player.getPlayerID())) {
                score += LONGEST_PATH_POINTS;
            }
            Set<Player> players = scores.getOrDefault(score, new HashSet<>());
            players.add(player);
            scores.put(score, players);
        }
        // all the eliminated players have 0 points
        if (eliminatedPlayers.size() > 0) {
            scores.put(0, new HashSet<>(eliminatedPlayers));
        }
        List<Integer> scoresList = new ArrayList<>(scores.keySet());
        Collections.sort(scoresList);
        Collections.reverse(scoresList); // in descending order, from highest to lowest.

        List<Set<Player>> ranking = new ArrayList<>();
        for (Integer score : scoresList) {
            ranking.add(scores.get(score));
        }

        return ranking;
    }

    /**
     * Calculates the score for a given player based on the connections they have acquired and if
     * they have connected all of their destinations. Does NOT take into account if the player
     * has the longest path.
     * @param player the player to get the score of
     * @return an integer representing the score
     */
    private int calculatePlayerScoreWithoutBonus(Player player) {
        return calculatePointsForAcquiredSegments(player) + calculatePlayerDestinationPoints(player);
    }

    /**
     * Returns the points that the given player earned for acquiring their connections based on
     * the connections' lengths
     * @param player the given player
     * @return the number of points the player gets for acquiring connections
     */
    private int calculatePointsForAcquiredSegments(Player player) {
        int points = 0;
        for (Connection connection : getPlayerAcquiredConnections(player)) {
            points += connection.getLength();
        }
        return points;
    }

    /**
     * Returns the number of points to add or subtract from a player's score based on whether
     * they have connected their destinations in the game
     * @param player the Player to calculate the points for
     * @return the points to add/subtract from a player's score
     */
    private int calculatePlayerDestinationPoints(Player player) {
        int points = 0;
        Set<Destination> destinations = player.getInventory().getDestinations();
        for (Destination destination : destinations) {
            if (playerDestinationReached(player, destination)) {
                points += DESTINATION_REACHED_POINTS;
            }
            else {
                points -= DESTINATION_REACHED_POINTS;
            }
        }
        return points;
    }

    /**
     * Determines if the given player has connected the given Destination with a series of connections
     * @param player the given Player
     * @param destination the given Destination
     * @return whether the player had connected the given Destination
     */
    private boolean playerDestinationReached(Player player, Destination destination) {
        City origin = destination.getCity1();
        City end = destination.getCity2();
        Set<Connection> playerConnections = getPlayerAcquiredConnections(player);
        return routeExistsBetweenCities(origin, end, playerConnections);
    }

    // TODO TAKEN FROM TRAINSMAP, NEED TO PUT INTO UTIL?
    /**
     * Checks if there is a route that exists between 2 given cities in the given connections
     * @param city1 String representing the name of the first city in the route
     * @param city2 String representing the name of the other city in the route
     * @param connections Set of Connection representing the connections to look through for the cities
     * @return true if there is a route between the cities, false otherwise
     */
    public boolean routeExistsBetweenCities(City city1, City city2, Set<Connection> connections) {
        return this.routeExistsBetweenCitiesAcc(city1, city2, connections, new HashSet<>());
    }

    // TODO TAKEN FROM TRAINSMAP, NEED TO PUT INTO UTIL?
    /**
     * Accumulator method for routeExistsBetweenCities.
     * @param city1 City representing the first city in the route
     * @param city2 City representing the other city in the route
     * @param acc Set of Connection representing an accumulator for connections we've already checked
     * @return true if there is a route between the cities, false if not
     */
    private boolean routeExistsBetweenCitiesAcc(City city1, City city2, Set<Connection> connections, Set<Connection> acc) {
        for (Connection connection : connections) {
            // if we've already checked this connection, skip
            if (!acc.contains(connection)) {
                List<City> endpoints = new ArrayList<>(connection.getCities());
                // if the 2 cities have a direct connection, return true
                if (endpoints.contains(city1)) {
                    if (endpoints.contains(city2)) {
                        return true;
                    }
                    // otherwise, recur on the city that city1 is connected to in order to check if
                    // there is a route from city1 to city2 through the other city in this connection
                    else {
                        acc.add(connection);
                        if (endpoints.get(0).equals(city1)) {
                            if (this.routeExistsBetweenCitiesAcc(endpoints.get(1), city2, connections, acc)) {
                                return true;
                            }
                        } else {
                            if (this.routeExistsBetweenCitiesAcc(endpoints.get(0), city2, connections, acc)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns the id(s) of the player(s) that have the longest route/path in the game
     * @return the Set of Integer representing the player(s)'s ids with the longest path
     */
    private Set<Integer> getIDsofPlayersWithLongestPath() {
        Map<Integer, Set<Integer>> pathLengthToPlayers = new HashMap<>();
        for (Player player : activePlayers) {
            int playerLongestPath = longestPathLengthForPlayer(getPlayerAcquiredConnections(player));
            Set<Integer> playersWithScore = pathLengthToPlayers.getOrDefault(playerLongestPath, new HashSet<>());
            playersWithScore.add(player.getPlayerID());
            pathLengthToPlayers.put(playerLongestPath, playersWithScore);
        }
        int longestPath = Collections.max(pathLengthToPlayers.keySet());
        return pathLengthToPlayers.getOrDefault(longestPath, new HashSet<>());
    }

    /**
     * Returns the longest path within the given Connections
     * @param playerAcquiredConnections the acquired connections of a player
     * @return the length of the longest path within the given connections (length == # total rail segments)
     */
    private int longestPathLengthForPlayer(Set<Connection> playerAcquiredConnections) {
        Set<City> connectionCities = new HashSet<>();
        for (Connection connection : playerAcquiredConnections) {
            connectionCities.addAll(connection.getCities());
        }
        return DFS(connectionCities, playerAcquiredConnections);
    }

    // TODO DOESN'T WORK
    /**
     * Runs a depth first search to find the longest path for every given city as a starting vertex.
     * @param origins the set of cities to start running the DFS on
     * @param connections the set of acquired connections for a player
     * @return an integer representing the longest path
     */
    private int DFS(Set<City> origins, Set<Connection> connections) {
        List<Integer> maxPathFromCity = new ArrayList<>();
        for (City origin : origins) {
            maxPathFromCity.add(DFSacc(origin, connections, new HashSet<>()));
        }
        if (maxPathFromCity.size() == 0) {
            return 0;
        }
        Collections.sort(maxPathFromCity);
        Collections.reverse(maxPathFromCity);
        return maxPathFromCity.get(0);
    }

    /**
     * Runs a depth first search starting with the given city as the origin vertex.
     * @param origin the City origin to start running the DFS on
     * @param connections the set of acquired connections for a player
     * @param visitedCities the set of cities we
     * @return an integer representing the longest path
     */
    private int DFSacc(City origin, Set<Connection> connections, Set<City> visitedCities) {
        visitedCities.add(origin);

        // Find all cities adjacent to the origin city
        Set<Connection> adjacentConnections = new HashSet<>();
        for (Connection connection: connections) {
            if (connection.getCities().contains(origin)) {
                adjacentConnections.add(connection);
            }
        }
        Set<City> adjacentCities = new HashSet<>();
        for (Connection connection : adjacentConnections) {
            adjacentCities.addAll(connection.getCities());
            adjacentCities.remove(origin);
            for (City city : adjacentCities) {
                if (!visitedCities.contains(city)) {
                    return connection.getLength() + DFSacc(city, connections, visitedCities);
                }
            }
        }
        return 0;
    }

    /**
     * Returns all the connections that the given player has acquired in this game
     * @param player the Player to get the acquired connections of
     * @return the Set of Connections that are acquired by the given player
     */
    private Set<Connection> getPlayerAcquiredConnections(Player player) {
        Set<Connection> acquiredConnections = new HashSet<>();
        int playerId = player.getPlayerID();
        Map<Connection, Integer> connectionStatuses = this.gameState.getConnectionsStatus();
        for (Connection connection : connectionStatuses.keySet()) {
            if (connectionStatuses.get(connection) == playerId) {
                acquiredConnections.add(connection);
            }
        }
        return acquiredConnections;
    }
}
