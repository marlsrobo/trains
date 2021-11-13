package tournament_manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import java.util.Map.Entry;
import java.util.Set;
import map.ITrainMap;
import map.TrainMap;
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
    private Set<String> cheaters = new HashSet<>();


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
                TournamentResult roundReport = this.runOneRound(tournamentMap, this.remainingPlayers);
                this.updatePlayerStatusesAfterRound(players, roundReport);
                break;
            }
            // else, keep running rounds of games
            TournamentResult roundReport = this.runOneRound(tournamentMap, this.remainingPlayers);
            this.updatePlayerStatusesAfterRound(players, roundReport);
        }
        TournamentResult result = new TournamentResult(this.remainingPlayers.keySet(), this.cheaters);
        reportTournamentResultToPlayers(result, players);
        return result;
    }

    /**
     * After a round of games has finished, updates the remaining players in the tournament as the
     * winners of the last round, and adds any cheaters to the aggregate list of cheating players
     * @param players the initial list of players who were selected for the tournament
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
        // TODO IMPLEMENT
        Set<ITrainMap> submittedMaps;
        return new TrainMap(new HashSet<>(), new HashSet<>());
    }

    /**
     * Determines if the tournament is over. The tournament is over when two tournament rounds
     * of games in a row produce the exact same winners, or when there are too few players for
     * a single game.
     * @return whether the tournament should end
     */
    private boolean isTournamentOver() {
        return this.remainingPlayers == this.previousRemainingPlayers
                || this.remainingPlayers.size() < MIN_PLAYERS_PER_GAME;
    }

    private boolean timeToRunLastRound() {
        return this.remainingPlayers.size() >= MIN_PLAYERS_PER_GAME
                && this.remainingPlayers.size() <= MAX_PLAYERS_PER_GAME;
    }

    /**
     * Run a single round of game(s) and report the winners and cheaters from the round.
     * @param map the map used for the round of games
     * @param remainingPlayers the remaining players left in the tournament which will be split
     *                         into individual games for the round
     * @return the TournamentResult of the round containing the names of the winner(s) and cheater(s)
     */
    private TournamentResult runOneRound(ITrainMap map,
        LinkedHashMap<String, IPlayer> remainingPlayers) {

        List<LinkedHashMap<String, IPlayer>> gameAllocation = allocatePlayersToGames(
            remainingPlayers);
        Set<String> winnerNames = new HashSet<>();
        Set<String> cheaterNames = new HashSet<>();

        for (LinkedHashMap<String, IPlayer> game : gameAllocation) {
            // TODO: pass in methods to shuffle destinations and return deck to ref here
            IReferee ref = new RefereeBuilder(map, game).build();
            ref.playGame();
            GameEndReport report = ref.calculateGameEndReport();
            winnerNames.addAll(report.getWinners());
            cheaterNames.addAll(report.removedPlayerNames);
        }

        return new TournamentResult(winnerNames, cheaterNames);
    }

    /**
     * Allocated the given players into a set of games for a single round of games
     * @param remainingPlayers the players to be divided into games
     * @return the players divided into games (each item in the list represents a single game's players)
     */
    private List<LinkedHashMap<String, IPlayer>> allocatePlayersToGames(
            LinkedHashMap<String, IPlayer> remainingPlayers) {

        List<LinkedHashMap<String, IPlayer>> result = new ArrayList<>();

        int leftoverPlayers = remainingPlayers.size() % MAX_PLAYERS_PER_GAME;
        int numFullGames = remainingPlayers.size() / MAX_PLAYERS_PER_GAME;
        if (leftoverPlayers < MIN_PLAYERS_PER_GAME && leftoverPlayers != 0) {
            numFullGames -= 1;
        }

        Iterator<Entry<String, IPlayer>> playersInAgeOrder = remainingPlayers.entrySet().iterator();
        for (int ii = 0; ii < numFullGames; ii++) {
            LinkedHashMap<String, IPlayer> playersInGame = new LinkedHashMap<>();
            for (int jj = 0; jj < MAX_PLAYERS_PER_GAME; jj++) {
                Entry<String, IPlayer> player = playersInAgeOrder.next();
                playersInGame.put(player.getKey(), player.getValue());
            }
            result.add(playersInGame);
        }

        LinkedHashMap<String, IPlayer> playersInFinalGame = new LinkedHashMap<>();
        while (playersInAgeOrder.hasNext()) {
            Entry<String, IPlayer> player = playersInAgeOrder.next();

            playersInFinalGame.put(player.getKey(), player.getValue());
        }
        result.add(playersInFinalGame);

        return result;
    }

    /**
     * Informs each of the given players if they won or lost the tournament
     * @param result the TournamentResult containing the names of the winner(s) and cheater(s)
     *               of the tournament
     * @param players the players who participated in the tournament
     */
    private void reportTournamentResultToPlayers(TournamentResult result,
                                                 LinkedHashMap<String, IPlayer> players) {
        for (Entry<String, IPlayer> player : players.entrySet()) {
            player.getValue().resultOfTournament(result.getWinners().contains(player.getKey()));
        }
    }
}
