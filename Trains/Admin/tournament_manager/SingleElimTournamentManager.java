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
                TournamentResult roundReport = this.runOneRound(tournamentMap);
                this.updatePlayerStatusesAfterRound(players, roundReport);
                break;
            }
            // else, keep running rounds of games
            TournamentResult roundReport = this.runOneRound(tournamentMap);
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

    /**
     * Determines if a final round of games (of a single game) should be played. Returns true
     * when there are only enough players left in the tournament for a single game.
     * @return whether the final round should begin in the tournament
     */
    private boolean timeToRunLastRound() {
        return this.remainingPlayers.size() >= MIN_PLAYERS_PER_GAME
                && this.remainingPlayers.size() <= MAX_PLAYERS_PER_GAME;
    }

    /**
     * Run a single round of game(s) with the remaining players and report the winners and cheaters from the round.
     * @param map the map used for the round of games
     * @return the TournamentResult of the round containing the names of the winner(s) and cheater(s)
     */
    private TournamentResult runOneRound(ITrainMap map) {

        List<LinkedHashMap<String, IPlayer>> gameAllocation = allocatePlayersToGames();
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
     * Allocated the remaining players into a set of games for a single round of games
     * @return the players divided into games (each item in the list represents a single game's players)
     */
    private List<LinkedHashMap<String, IPlayer>> allocatePlayersToGames() {

//        List<LinkedHashMap<String, IPlayer>> result = new ArrayList<>();

        int numFullGames = numGamesWithMaxPlayers();

        Iterator<Entry<String, IPlayer>> playersInAgeOrder = this.remainingPlayers.entrySet().iterator();

        List<LinkedHashMap<String, IPlayer>> result = addPlayersToFullGames(numFullGames, playersInAgeOrder);
        // if every game didn't have a max number of players, make an additional game for the round
        if (playersInAgeOrder.hasNext()) {
            result.add(addPlayersInFinalGame(playersInAgeOrder));
        }
        return result;



//        for (int ii = 0; ii < numFullGames; ii++) {
//            LinkedHashMap<String, IPlayer> playersInGame = new LinkedHashMap<>();
//            for (int jj = 0; jj < MAX_PLAYERS_PER_GAME; jj++) {
//                Entry<String, IPlayer> player = playersInAgeOrder.next();
//                playersInGame.put(player.getKey(), player.getValue());
//            }
//            result.add(playersInGame);
//        }

//        LinkedHashMap<String, IPlayer> playersInFinalGame = new LinkedHashMap<>();
//        while (playersInAgeOrder.hasNext()) {
//            Entry<String, IPlayer> player = playersInAgeOrder.next();
//
//            playersInFinalGame.put(player.getKey(), player.getValue());
//        }
//        result.add(playersInFinalGame);
//
//        return result;
    }

    /**
     * Allocates the given players into #numFullGames games of MAX_PLAYERS_PER_GAME size
     * @param numFullGames the number of full games to create
     * @param playersInAgeOrder the players to allocate into the games (provided in the desired order)
     * @return the players split into individual games
     */
    private List<LinkedHashMap<String, IPlayer>> addPlayersToFullGames(
            int numFullGames,
            Iterator<Entry<String, IPlayer>> playersInAgeOrder) {

        List<LinkedHashMap<String, IPlayer>> result = new ArrayList<>();
        for (int ii = 0; ii < numFullGames; ii++) {
            LinkedHashMap<String, IPlayer> playersInGame = new LinkedHashMap<>();
            for (int jj = 0; jj < MAX_PLAYERS_PER_GAME; jj++) {
                Entry<String, IPlayer> player = playersInAgeOrder.next();
                playersInGame.put(player.getKey(), player.getValue());
            }
            result.add(playersInGame);
        }
        return result;
    }

    /**
     * Create a final game for a round of games with the remaining players left over after
     * allocating the previous players into full games
     * @param playersInAgeOrder the remaining players to be allocated
     * @return the final game for the round of games containing the players in the game
     */
    private LinkedHashMap<String, IPlayer> addPlayersInFinalGame(Iterator<Entry<String, IPlayer>> playersInAgeOrder) {
        LinkedHashMap<String, IPlayer> playersInFinalGame = new LinkedHashMap<>();
        while (playersInAgeOrder.hasNext()) {
            Entry<String, IPlayer> player = playersInAgeOrder.next();

            playersInFinalGame.put(player.getKey(), player.getValue());
        }
        return playersInFinalGame;
    }

    /**
     * Calculates the number of games that have the maximum number of players for a round of Trains
     * @return the number of games with max number of players
     */
    private int numGamesWithMaxPlayers() {
        int leftoverPlayers = this.remainingPlayers.size() % MAX_PLAYERS_PER_GAME;
        int numFullGames = this.remainingPlayers.size() / MAX_PLAYERS_PER_GAME;
        if (leftoverPlayers < MIN_PLAYERS_PER_GAME && leftoverPlayers != 0) {
            numFullGames -= 1;
        }
        return numFullGames;
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
