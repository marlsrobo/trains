package referee;

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

  /** List of players who were not removed, in descending order of their score. */
  public List<PlayerScore> playerRanking;

  /** The indices of removed players. */
  public Set<Integer> removedPlayerTurnOrderIndices;

  /**
   * Constructs this report directly from the given data - no defensive copies are made since this
   * is a simple value class.
   *
   * @param playerRanking the ranking of the players and their scores.
   * @param removedPlayerTurnOrderIndices the indices of removed players.
   */
  public GameEndReport(
      List<PlayerScore> playerRanking, Set<Integer> removedPlayerTurnOrderIndices) {
    this.playerRanking = playerRanking;
    this.removedPlayerTurnOrderIndices = removedPlayerTurnOrderIndices;
  }

  /** Value class for associating a score to a player (identified by an index). */
  public static class PlayerScore {

    /** The index identifying the player. */
    public int playerTurnOrderIndex;

    /** Score of the player. */
    public int score;

    /**
     * Constructs this from given inputs without validation.
     *
     * @param playerTurnOrderIndex index of identifying player.
     * @param score score of the player.
     */
    public PlayerScore(int playerTurnOrderIndex, int score) {
      this.playerTurnOrderIndex = playerTurnOrderIndex;
      this.score = score;
    }
  }
}
