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
import referee.TrainsReferee;
import referee.TrainsReferee.RefereeBuilder;

public class SingleElimTournamentManager implements ITournamentManager {

    private final static int MIN_PLAYERS_PER_GAME = 2;
    private final static int MAX_PLAYERS_PER_GAME = 8;


    /**
     * Begins the tournament with the given players with their corresponding names
     *
     * @param players the players who have been selected for the tournament
     * @return the winner(s) of the tournament and a list of the misbehaving players
     */
    @Override
    public TournamentResult runTournament(LinkedHashMap<String, IPlayer> players) {
        ITrainMap tournamentMap = startTournament(players);
        Set<String> cheaters = new HashSet<>();
        LinkedHashMap<String, IPlayer> remainingPlayers = new LinkedHashMap<>(players);
        LinkedHashMap<String, IPlayer> previousRemainingPlayers = new LinkedHashMap<>();

        while (!isTournamentOver(remainingPlayers, previousRemainingPlayers)) {
            TournamentResult roundReport = this.runOneRound(tournamentMap, remainingPlayers);
            previousRemainingPlayers = remainingPlayers;
            remainingPlayers = new LinkedHashMap<>();
            for (String winnerName : roundReport.winners) {
                remainingPlayers.put(winnerName, players.get(winnerName));
            }
            cheaters.addAll(roundReport.cheaters);
        }
        if (remainingPlayers.size() >= MIN_PLAYERS_PER_GAME
            && remainingPlayers.size() <= MAX_PLAYERS_PER_GAME) {
            TournamentResult roundReport = this.runOneRound(tournamentMap, remainingPlayers);
            remainingPlayers = new LinkedHashMap<>();
            for (String winnerName : roundReport.winners) {
                remainingPlayers.put(winnerName, players.get(winnerName));
            }
            cheaters.addAll(roundReport.cheaters);
        }
        TournamentResult result = new TournamentResult(remainingPlayers.keySet(), cheaters);
        reportTournamentResultToPlayers(result, players);
        return result;
    }

    /**
     * Calls start on each player and picks a TrainsMap to use for all the games in the tournament
     *
     * @param players the players that will be playing in the tournament
     * @return the chosen map for the tournament
     */
    private ITrainMap startTournament(LinkedHashMap<String, IPlayer> players) {
        Set<ITrainMap> submittedMaps;
        return new TrainMap(new HashSet<>(), new HashSet<>());
    }

    /**
     * Determines if the tournament is over
     *
     * @param remainingPlayers
     * @param previousRemainingPlayers
     * @return
     */
    private boolean isTournamentOver(LinkedHashMap<String, IPlayer> remainingPlayers,
        LinkedHashMap<String, IPlayer> previousRemainingPlayers) {
        return remainingPlayers == previousRemainingPlayers
            || remainingPlayers.size() < MIN_PLAYERS_PER_GAME
            || remainingPlayers.size() <= MAX_PLAYERS_PER_GAME;
    }

    private TournamentResult runOneRound(ITrainMap map,
        LinkedHashMap<String, IPlayer> remainingPlayers) {

        List<LinkedHashMap<String, IPlayer>> gameAllocation = allocatePlayersToGames(
            remainingPlayers);
        TournamentResult roundResult = new TournamentResult();

        for (LinkedHashMap<String, IPlayer> game : gameAllocation) {
            IReferee ref = new RefereeBuilder(map, game).build();

            ref.playGame();
            GameEndReport report = ref.calculateGameEndReport();
            roundResult.winners.addAll(report.getWinners());
            roundResult.cheaters.addAll(report.removedPlayerNames);
        }

        return roundResult;
    }

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
        for (Iterator<Entry<String, IPlayer>> it = playersInAgeOrder; it.hasNext(); ) {
            Entry<String, IPlayer> player = it.next();

            playersInFinalGame.put(player.getKey(), player.getValue());
        }
        result.add(playersInFinalGame);

        return result;
    }

    private void reportTournamentResultToPlayers(TournamentResult result,
        LinkedHashMap<String, IPlayer> players) {

        for (Entry<String, IPlayer> player : players.entrySet()) {
            player.getValue().tournamentResult(result.winners.contains(player.getKey()));
        }
    }
}
