package tournament_manager;

import java.util.LinkedHashMap;

import player.IPlayer;

/**
 * Represents a tournament manager for the Trains games which is in charge of beginning the
 * tournament, handling which players are selected for specific games, and reporting the result of
 * the tournament.
 */
public interface ITournamentManager {

    /**
     * Runs the tournament with the given players with their corresponding names
     *
     * @param players the players who have been selected for the tournament
     * @return the winner(s) of the tournament and a list of the misbehaving players
     */
    TournamentResult runTournament(LinkedHashMap<String, IPlayer> players);
}
