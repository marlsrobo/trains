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
    private List<PlayerScore> playerRanking;

    /**
     * The indices of removed players.
     */
    private Set<String> removedPlayerNames;

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
        private String playerName;

        /**
         * Score of the player.
         */
        private int score;

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

        public String getPlayerName() {
            return playerName;
        }

        public int getScore() {
            return score;
        }

        @Override
        public String toString() {
            return this.playerName + " " + this.score;
        }
    }

    public List<PlayerScore> getPlayerRanking() {
        return new ArrayList<>(this.playerRanking);
    }

    public Set<String> getRemovedPlayerNames() {
        return new HashSet<>(this.removedPlayerNames);
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

    @Override
    public String toString() {
        return "ranking: " + this.playerRanking.toString() + "\n" +
                "cheaters: " + this.removedPlayerNames.toString();
    }
}
