package tournament_manager;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Representation of the result of either a single round of a tournament, or the result of the
 * entire tournament, which contains the winners of the round/tournament, and the cheaters of the
 * round/tournament.
 */
public class TournamentResult {

    private final Set<String> winners;
    private final Set<String> cheaters;

    /**
     * Basic constructor for a TournamentResult
     * @param winners the names of the winners of the round/tournament
     * @param cheaters the names of the cheaters who were eliminated from the round/tournament
     */
    public TournamentResult(Set<String> winners, Set<String> cheaters) {
        Objects.requireNonNull(winners);
        Objects.requireNonNull(cheaters);

        this.winners = new HashSet<>(winners);
        this.cheaters = new HashSet<>(cheaters);
    }

    /**
     * Getter for the winners
     * @return the names of the player(s) who won the round/tournament
     */
    public Set<String> getWinners() {
        return new HashSet<>(this.winners);
    }

    /**
     * Getter for the cheaters
     * @return the names of the player(s) who cheated and were eliminated from the round/tournament
     */
    public Set<String> getCheaters() {
        return new HashSet<>(this.cheaters);
    }
}
