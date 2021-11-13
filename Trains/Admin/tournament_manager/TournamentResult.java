package tournament_manager;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TournamentResult {

    public Set<String> winners;

    public Set<String> cheaters;

    public TournamentResult(Set<String> winners, Set<String> cheaters) {
        Objects.requireNonNull(winners);
        Objects.requireNonNull(cheaters);

        this.winners = new HashSet<>(winners);
        this.cheaters = new HashSet<>(cheaters);
    }

    public TournamentResult() {
        this(new HashSet<>(), new HashSet<>());
    }

}
