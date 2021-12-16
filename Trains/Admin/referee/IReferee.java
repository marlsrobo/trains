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
     * Runs the entire game from start to finish except for calculating the results. This method
     * should be run only once.
     * <p>
     * After this method is called, the results of the game will be available by calling
     * calculateGameEndReport.
     */
    void playGame();

    /**
     * Calculates the scores of each player in the game and ranks them by descending order of score.
     * Players that were removed from the game due to cheating are including separately in the game
     * report.
     *
     * @return the rankings, scores, and removed players as a GameEndReport.
     */
    GameEndReport calculateGameEndReport();


}
