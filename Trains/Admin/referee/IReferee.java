package referee;

/**
 * Represents a game-agnostic referee that is capable of running a game from a single method call
 * and providing a ranking/scoring of participating players and the set of players that were
 * removed.
 *
 * <p>Specific implementations of the referee will exist for different games or variants of games.
 */
public interface IReferee {

  /**
   * Single call to this method plays the entire game from start to finish and yields a report of
   * the rankings, scores, and removed players.
   *
   * @return the rankings, scores, and removed players as a GameEndReport.
   */
  void playGame();

  /**
   * Calculates the scores of each player in the game and ranks them by ascending order of score.
   * Players that were removed from the game due to cheating are including separately in the game report.
   * @return a container for the game report
   */
  GameEndReport calculateGameEndReport();


}
