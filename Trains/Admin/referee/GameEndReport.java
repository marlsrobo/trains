package referee;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A value class for a game-agnostic report of the players' scores, rankings, and removal status.
 *
 * <p>Players are identified by their integer index in a list of players that is maintained by the
 * creator of this object. No validity checks are made to ensure validity of scores, indices, or
 * uniqueness of indices since this is a simple value class.
 */
public class GameEndReport {

    /**
     * List of players who were not removed, in descending order of their score.
     */
    public List<PlayerScore> playerRanking;

    /**
     * The indices of removed players.
     */
    public Set<String> removedPlayerNames;

    /**
     * Constructs this report directly from the given data - no defensive copies are made since this
     * is a simple value class.
     *
     * @param playerRanking      the ranking of the players and their scores.
     * @param removedPlayerNames the names of removed players.
     */
    public GameEndReport(
        List<PlayerScore> playerRanking, Set<String> removedPlayerNames) {
        this.playerRanking = playerRanking;
        this.removedPlayerNames = removedPlayerNames;
    }

    /**
     * Value class for associating a score to a player (identified by an index).
     */
    public static class PlayerScore {

        /**
         * The index identifying the player.
         */
        public String playerName;

        /**
         * Score of the player.
         */
        public int score;

        /**
         * Constructs this from given inputs without validation.
         *
         * @param playerName name of identifying player.
         * @param score      score of the player.
         */
        public PlayerScore(String playerName, int score) {
            this.playerName = playerName;
            this.score = score;
        }
    }

    public Set<String> getWinners() {
        if (this.playerRanking.size() < 1) {
            return new HashSet<>();
        }

        Set<String> winnerNames = new HashSet<>();
        winnerNames.add(this.playerRanking.get(0).playerName);
        for (int i = 1; i < this.playerRanking.size(); i++) {
            if (this.playerRanking.get(i).score == this.playerRanking.get(0).score) {
                winnerNames.add(this.playerRanking.get(i).playerName);
            } else {
                break;
            }
        }
        return winnerNames;
    }
}
