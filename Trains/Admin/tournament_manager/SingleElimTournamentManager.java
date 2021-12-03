package tournament_manager;

import static tournament_manager.PlayerAllocator.allocatePlayersToGames;
import static utils.Constants.PLAYER_INTERACTION_TIMEOUT;
import static utils.Utils.callFunction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

import game_state.RailCard;
import map.Destination;
import map.ITrainMap;
import player.IPlayer;
import referee.GameEndReport;
import referee.IReferee;
import referee.TrainsReferee.RefereeBuilder;

import test_utils.*;
import utils.Constants;

/**
 * A Tournament Manager for the game Trains that uses single elimination bracket for tournament
 * rounds.
 * <p>
 * Handles notifying Players that they have been chosen for a tournament, deciding which map to use
 * for the tournament, allocating Players to games and creating Referees, running the tournament,
 * and reporting the result of the entire tournament.
 */
public class SingleElimTournamentManager implements ITournamentManager {

    private LinkedHashMap<String, IPlayer> remainingPlayers;
    private LinkedHashMap<String, IPlayer> previousRemainingPlayers = new LinkedHashMap<>();
    private final Set<String> cheaters = new HashSet<>();

    private final Function<ITrainMap, List<Destination>> destinationProvider;
    private final Supplier<List<RailCard>> deckSupplier;
    private final Function<List<ITrainMap>, ITrainMap> mapSelector;

    private SingleElimTournamentManager(Function<ITrainMap, List<Destination>> destinationProvider,
        Supplier<List<RailCard>> deckSupplier,
        Function<List<ITrainMap>, ITrainMap> mapSelector) {
        this.destinationProvider = destinationProvider;
        this.deckSupplier = deckSupplier;
        this.mapSelector = mapSelector;
    }

    /**
     * Used to construct instances of SingleElimTournamentManager allowing any subset of its
     * customizable features to be set.
     */
    public static class SingleElimTournamentManagerBuilder {

        private Function<ITrainMap, List<Destination>> destinationProvider;
        private Supplier<List<RailCard>> deckProvider;
        private Function<List<ITrainMap>, ITrainMap> mapSelector;

        /**
         * Construct the default builder for instances of SingleElimTournamentManager.
         */
        public SingleElimTournamentManagerBuilder() {
            this.destinationProvider = TrainsMapUtils::defaultDestinationProvider;
            this.deckProvider = TrainsMapUtils::defaultDeckSupplier;
            this.mapSelector = TrainsMapUtils::defaultMapSelector;
        }

        /**
         * Sets the destination provider function that will be used in all games of this
         * tournament.
         * <p>
         * A destination provider function accepts an ITrainMap, and returns the order to hand the
         * destinations in that map to players.
         *
         * @param destinationProvider A valid destination provider function.
         * @return This builder modified to use the provided destination provider function.
         */
        public SingleElimTournamentManagerBuilder destinationProvider(
            Function<ITrainMap, List<Destination>> destinationProvider) {
            this.destinationProvider = destinationProvider;
            return this;
        }

        /**
         * Sets the deck provider function that will be used in all games of this tournament.
         * <p>
         * A deck provider function accepts no arguments, and returns a list of RailCards in the
         * order that they should be provided to players when they draw from the deck.
         *
         * @param deckProvider A valid deck provider function.
         * @return This builder modified to use the provided deck provider function.
         */
        public SingleElimTournamentManagerBuilder deckProvider(
            Supplier<List<RailCard>> deckProvider) {
            this.deckProvider = deckProvider;
            return this;
        }

        /**
         * Sets the map selector function that will be used by this tournament manager.
         * <p>
         * A map selector function accepts a non-empty list of ITrainMaps, and returns the one that
         * should be used for all games in this tournament.
         *
         * @param mapSelector A valid map selector function.
         * @return This builder modified to use the provided map selector function.
         */
        public SingleElimTournamentManagerBuilder mapSelector(
            Function<List<ITrainMap>, ITrainMap> mapSelector) {
            this.mapSelector = mapSelector;
            return this;
        }

        /**
         * Constructs a SingleElimTournamentManager from the optional arguments given to this
         * builder.
         *
         * @return a SingleElimTournamentManager constructed with the given arguments.
         */
        public SingleElimTournamentManager build() {
            Objects.requireNonNull(this.deckProvider);
            Objects.requireNonNull(this.destinationProvider);
            Objects.requireNonNull(this.mapSelector);

            return new SingleElimTournamentManager(
                this.destinationProvider, this.deckProvider, this.mapSelector);
        }
    }


    /**
     * Runs a single elimination tournament with the given players. All players that do not place
     * first in their game of Trains will be eliminated.
     * <p>
     * The tournament will end when:
     * <ol>
     *     <li>Two rounds produce the same winners</li>
     *     <li>The number of remaining players is less than the number of players required to play
     *     one game</li>
     *     <li>If the number of players is less than the maximum number able to play one game, they
     *     will play one game as the final round</li>
     * </ol>
     *
     * @param players the players who have been selected for the tournament
     * @return the winner(s) of the tournament and a list of the misbehaving players
     */
    @Override
    public TournamentResult runTournament(LinkedHashMap<String, IPlayer> players) {
        Objects.requireNonNull(players);
        for (Entry<String, IPlayer> player : players.entrySet()) {
            Objects.requireNonNull(player.getKey());
            Objects.requireNonNull(player.getValue());
        }

        this.remainingPlayers = new LinkedHashMap<>(players);
        ITrainMap tournamentMap = getMapToStartTournament(players);

        while (!isTournamentOver()) {
            // if there are only enough players left for a single game, run the last game
            if (timeToRunLastRound()) {
                TournamentResult roundReport = this.runOneRound(tournamentMap);
                this.updatePlayerStatusesAfterRound(players, roundReport);
                break;
            }
            // else, keep running rounds of games
            TournamentResult roundReport = this.runOneRound(tournamentMap);
            this.updatePlayerStatusesAfterRound(players, roundReport);
        }
        TournamentResult result = new TournamentResult(this.remainingPlayers.keySet(),
            this.cheaters);
        reportTournamentResultToPlayers(result, players);
        return result;
    }

    /**
     * After a round of games has finished, updates the remaining players in the tournament as the
     * winners of the last round, and adds any cheaters to the aggregate list of cheating players
     *
     * @param players     the initial list of players who were selected for the tournament
     * @param roundReport the report of winners and cheaters from the round of games just played
     */
    private void updatePlayerStatusesAfterRound(LinkedHashMap<String, IPlayer> players,
        TournamentResult roundReport) {
        this.previousRemainingPlayers = this.remainingPlayers;
        this.remainingPlayers = new LinkedHashMap<>();
        for (String winnerName : roundReport.getWinners()) {
            this.remainingPlayers.put(winnerName, players.get(winnerName));
        }
        this.cheaters.addAll(roundReport.getCheaters());
    }

    /**
     * Calls start on each player and picks one of the reurned TrainsMaps to use for all the games
     * in the tournament.
     *
     * @param players the players that will be playing in the tournament
     * @return the chosen map for the tournament
     */
    private ITrainMap getMapToStartTournament(LinkedHashMap<String, IPlayer> players) {
        List<ITrainMap> submittedMaps = new ArrayList<>();
        for (Entry<String, IPlayer> player : players.entrySet()) {
            Callable<ITrainMap> startTournamentCallable = () -> player.getValue()
                .startTournament(true);

            Optional<ITrainMap> map = callFunction(startTournamentCallable);
            if (map.isPresent()) {
                submittedMaps.add(map.get());
            }
            else {
                this.cheaters.add(player.getKey());
                this.remainingPlayers.remove(player.getKey());
            }
        }
        if (submittedMaps.isEmpty()) {
            return TrainsMapUtils.createDefaultMap();
        }
        return this.mapSelector.apply(submittedMaps);
    }

    /**
     * Determines if the tournament is over. The tournament is over when two tournament rounds of
     * games in a row produce the exact same winners, or when there are too few players for a single
     * game.
     *
     * @return whether the tournament should end.
     */
    private boolean isTournamentOver() {
        return this.remainingPlayers.equals(this.previousRemainingPlayers)
            || this.remainingPlayers.size() < Constants.MIN_PLAYERS_PER_GAME;
    }

    /**
     * Determines if a final round of games (of a single game) should be played. Returns true when
     * there are only enough players left in the tournament for a single game.
     *
     * @return whether the final round should begin in the tournament.
     */
    private boolean timeToRunLastRound() {
        return this.remainingPlayers.size() >= Constants.MIN_PLAYERS_PER_GAME
            && this.remainingPlayers.size() <= Constants.MAX_PLAYERS_PER_GAME;
    }

    /**
     * Run a single round of game(s) with the remaining players and report the winners and cheaters
     * from the round.
     *
     * @param map the map used for the round of games.
     * @return the TournamentResult of the round containing the names of the winner(s) and
     * cheater(s).
     */
    private TournamentResult runOneRound(ITrainMap map) {
        List<LinkedHashMap<String, IPlayer>> gameAllocation = allocatePlayersToGames(
            this.remainingPlayers, Constants.MAX_PLAYERS_PER_GAME, Constants.MIN_PLAYERS_PER_GAME);
        Set<String> winnerNames = new HashSet<>();
        Set<String> cheaterNames = new HashSet<>();

        for (LinkedHashMap<String, IPlayer> game : gameAllocation) {
            IReferee ref = new RefereeBuilder(map, game)
                .deckProvider(this.deckSupplier)
                .destinationProvider(this.destinationProvider)
                .build();
            ref.playGame();
            GameEndReport report = ref.calculateGameEndReport();
            winnerNames.addAll(report.getWinners());
            cheaterNames.addAll(report.getRemovedPlayerNames());
        }
        return new TournamentResult(winnerNames, cheaterNames);
    }

    /**
     * Informs each of the given players if they won or lost the tournament
     *
     * @param result  the TournamentResult containing the names of the winner(s) and cheater(s) of
     *                the tournament
     * @param players the players who participated in the tournament
     */
    private void reportTournamentResultToPlayers(TournamentResult result,
        LinkedHashMap<String, IPlayer> players) {
        for (Entry<String, IPlayer> player : players.entrySet()) {
            Callable<Boolean> resultOfTournamentCallable = () -> {
                player.getValue().resultOfTournament(result.getWinners().contains(player.getKey()));
                return true;
            };
            if (!this.cheaters.contains(player.getKey())) {
                callFunction(resultOfTournamentCallable);
            }
        }
    }
}
