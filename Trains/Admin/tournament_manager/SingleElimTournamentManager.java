package tournament_manager;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import map.ITrainMap;
import map.TrainMap;
import player.IPlayer;

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
    LinkedHashMap<String, IPlayer> remainingPlayers = new LinkedHashMap<>(players);
    LinkedHashMap<String, IPlayer> previousRemainingPlayers = new LinkedHashMap<>();
    while (!isTournamentOver(remainingPlayers, previousRemainingPlayers)) {

    }
    if (remainingPlayers.size() >= MIN_PLAYERS_PER_GAME && remainingPlayers.size() <= MAX_PLAYERS_PER_GAME) {
      // run last round
    }
    TournamentResult result = new TournamentResult();
    // tell each player if they won or lost
    return result;
  }

  /**
   * Calls start on each player and picks a TrainsMap to use for all the games in the tournament
   * @param players the players that will be playing in the tournament
   * @return the chosen map for the tournament
   */
  private ITrainMap startTournament(LinkedHashMap<String, IPlayer> players) {
    return new TrainMap(new HashSet<>(), new HashSet<>());
  }

  /**
   * Determines if the tournament is over
   * @param remainingPlayers
   * @param previousRemainingPlayers
   * @return
   */
  private boolean isTournamentOver(LinkedHashMap<String, IPlayer> remainingPlayers,
                                   LinkedHashMap<String, IPlayer> previousRemainingPlayers) {
    return remainingPlayers == previousRemainingPlayers || remainingPlayers.size() < MIN_PLAYERS_PER_GAME
            || remainingPlayers.size() <= MAX_PLAYERS_PER_GAME;
  }

  private void runOneRound(ITrainMap map, LinkedHashMap<String, IPlayer> remainingPlayers) {

  }

  private List<LinkedHashMap<String, IPlayer>> allocatePlayersToGames(LinkedHashMap<String, IPlayer> remainingPlayers) {

  }


}
