package tournament_manager;

import static tournament_manager.PlayerAllocator.allocatePlayersToGames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import game_state.RailCard;
import map.Destination;
import map.ITrainMap;
import player.IPlayer;
import referee.GameEndReport;
import referee.IReferee;
import referee.TrainsReferee.RefereeBuilder;

/**
 * Representation of a tournament manager for a game of Trains that uses single elimination for
 * tournament rounds. Handles notifying Players that they have been chosen for a tournament,
 * allocating Players and Referees to games, deciding which map to use for the tournament, running
 * the tournament, getting the results of rounds of games, and reporting the result of the entire
 * tournament.
 */
public class SingleElimTournamentManager implements ITournamentManager {

    private final static int MIN_PLAYERS_PER_GAME = 2;
    private final static int MAX_PLAYERS_PER_GAME = 8;

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


    public static class SingleElimTournamentManagerBuilder {

        private static final int NUM_CARDS_IN_DECK = 250;

        private Function<ITrainMap, List<Destination>> destinationProvider;
        private Supplier<List<RailCard>> deckProvider;
        private Function<List<ITrainMap>, ITrainMap> mapSelector;


        public SingleElimTournamentManagerBuilder() {
            this.destinationProvider = SingleElimTournamentManagerBuilder::defaultDestinationProvider;
            this.deckProvider = SingleElimTournamentManagerBuilder::defaultDeckSupplier;
            this.mapSelector = SingleElimTournamentManagerBuilder::defaultMapSelector;
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

        private static ITrainMap defaultMapSelector(List<ITrainMap> maps) {
            if (maps.isEmpty()) {
                throw new IllegalArgumentException("Must be given at least 1 map to select");
            }
            return maps.get(0);
        }


        public SingleElimTournamentManagerBuilder destinationProvider(
            Function<ITrainMap, List<Destination>> destinationProvider) {
            this.destinationProvider = destinationProvider;
            return this;
        }


        public SingleElimTournamentManagerBuilder deckProvider(
            Supplier<List<RailCard>> deckProvider) {
            this.deckProvider = deckProvider;
            return this;
        }


        public SingleElimTournamentManagerBuilder mapSelector(
            Function<List<ITrainMap>, ITrainMap> mapSelector) {
            this.mapSelector = mapSelector;
            return this;
        }


        public SingleElimTournamentManager build() {
            Objects.requireNonNull(this.deckProvider);
            Objects.requireNonNull(this.destinationProvider);
            Objects.requireNonNull(this.mapSelector);

            return new SingleElimTournamentManager(
                this.destinationProvider, this.deckProvider, this.mapSelector);
        }
    }


    /**
     * Begins the tournament with the given players with their corresponding names
     *
     * @param players the players who have been selected for the tournament
     * @return the winner(s) of the tournament and a list of the misbehaving players
     */
    @Override
    public TournamentResult runTournament(LinkedHashMap<String, IPlayer> players) {
        ITrainMap tournamentMap = startTournament(players);
        this.remainingPlayers = new LinkedHashMap<>(players);

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
     * Calls start on each player and picks a TrainsMap to use for all the games in the tournament
     *
     * @param players the players that will be playing in the tournament
     * @return the chosen map for the tournament
     */
    private ITrainMap startTournament(LinkedHashMap<String, IPlayer> players) {
        List<ITrainMap> submittedMaps = new ArrayList<>();
        for (IPlayer player : players.values()) {
            submittedMaps.add(player.startTournament(true));
        }
        return this.mapSelector.apply(submittedMaps);
    }

    /**
     * Determines if the tournament is over. The tournament is over when two tournament rounds of
     * games in a row produce the exact same winners, or when there are too few players for a single
     * game.
     *
     * @return whether the tournament should end
     */
    private boolean isTournamentOver() {
        return this.remainingPlayers == this.previousRemainingPlayers
            || this.remainingPlayers.size() < MIN_PLAYERS_PER_GAME;
    }

    /**
     * Determines if a final round of games (of a single game) should be played. Returns true when
     * there are only enough players left in the tournament for a single game.
     *
     * @return whether the final round should begin in the tournament
     */
    private boolean timeToRunLastRound() {
        return this.remainingPlayers.size() >= MIN_PLAYERS_PER_GAME
            && this.remainingPlayers.size() <= MAX_PLAYERS_PER_GAME;
    }

    /**
     * Run a single round of game(s) with the remaining players and report the winners and cheaters
     * from the round.
     *
     * @param map the map used for the round of games
     * @return the TournamentResult of the round containing the names of the winner(s) and
     * cheater(s)
     */
    private TournamentResult runOneRound(ITrainMap map) {

        List<LinkedHashMap<String, IPlayer>> gameAllocation = allocatePlayersToGames(
            this.remainingPlayers, MAX_PLAYERS_PER_GAME, MIN_PLAYERS_PER_GAME);
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
            cheaterNames.addAll(report.removedPlayerNames);
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
            player.getValue().resultOfTournament(result.getWinners().contains(player.getKey()));
        }
    }
}
