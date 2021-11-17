import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import map.ITrainMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import player.IPlayer;
import player.Player;
import strategy.BuyNow;
import strategy.Cheat;
import tournament_manager.ITournamentManager;
import tournament_manager.SingleElimTournamentManager;
import tournament_manager.TournamentResult;

/**
 * Unit tests for a SingleElimTournamentManager
 */
public class TestSingleElimTournamentManager {

    String buyNowPath = "out/production/mark-twain/strategy/BuyNow.class";


    private ITrainMap mapSelector(List<ITrainMap> maps) {
        return maps.get(maps.size() - 1);
    }

    @Test
    public void testDefaultConstructionValid() {
        new SingleElimTournamentManager.SingleElimTournamentManagerBuilder().build();
    }

    @Test
    public void testConstructionValidDeckProvider() {
        new SingleElimTournamentManager.SingleElimTournamentManagerBuilder()
                .deckProvider(TestTrainsReferee::TenCardDeckSupplier).build();
    }

    @Test
    public void testConstructionValidDestinationProvider() {
        new SingleElimTournamentManager.SingleElimTournamentManagerBuilder()
                .destinationProvider(TestTrainsReferee::destinationProvider).build();
    }

    @Test
    public void testConstructionValidMapSelector() {
        new SingleElimTournamentManager.SingleElimTournamentManagerBuilder()
                .mapSelector(this::mapSelector).build();
    }

    @Test
    public void testInvalidNullConstructions() {
        assertThrows(NullPointerException.class,
                () -> new SingleElimTournamentManager.SingleElimTournamentManagerBuilder()
                .deckProvider(null).build());

        assertThrows(NullPointerException.class,
                () -> new SingleElimTournamentManager.SingleElimTournamentManagerBuilder()
                        .destinationProvider(null).build());

        assertThrows(NullPointerException.class,
                () -> new SingleElimTournamentManager.SingleElimTournamentManagerBuilder()
                        .mapSelector(null).build());
    }

    @Test
    public void testRunTournamentNullPlayers() {
        ITournamentManager manager = new SingleElimTournamentManager
                .SingleElimTournamentManagerBuilder().build();
        assertThrows(NullPointerException.class, () -> manager.runTournament(null));
    }

    @Test
    public void testRunTournamentNoPlayers() {
        ITournamentManager manager = new SingleElimTournamentManager
                .SingleElimTournamentManagerBuilder().build();
        TournamentResult actualResult = manager.runTournament(new LinkedHashMap<>());
        TournamentResult expectedResult = new TournamentResult(new HashSet<>(), new HashSet<>());
        Assertions.assertTrue(tournamentResultsEquals(actualResult, expectedResult));
    }

    @Test
    public void testRunTournamentOnePlayerGivesNullMap() {
        ITournamentManager manager = new SingleElimTournamentManager
                .SingleElimTournamentManagerBuilder().build();
        LinkedHashMap<String, IPlayer> playersInTurnOrder = new LinkedHashMap<>();
        playersInTurnOrder.put("marley", new Player(buyNowPath, null));
        TournamentResult actualResult = manager.runTournament(playersInTurnOrder);

        Set<String> cheaters = new HashSet<>();
        cheaters.add("marley");
        TournamentResult expectedResult = new TournamentResult(new HashSet<>(), cheaters);
        Assertions.assertTrue(tournamentResultsEquals(actualResult, expectedResult));
    }

    @Test
    public void testRunTournamentOnePlayerCheatingStrategy() {
        ITournamentManager manager = new SingleElimTournamentManager
                .SingleElimTournamentManagerBuilder().build();
        LinkedHashMap<String, IPlayer> playersInTurnOrder = new LinkedHashMap<>();
        playersInTurnOrder.put("marley", new Player(new Cheat()));
        TournamentResult actualResult = manager.runTournament(playersInTurnOrder);

        Set<String> ranking = new HashSet<>();
        ranking.add("marley");
        TournamentResult expectedResult = new TournamentResult(ranking, new HashSet<>());
        Assertions.assertTrue(tournamentResultsEquals(actualResult, expectedResult));
    }

    @Test
    public void testRunTournamentOnePlayerNotCheating() {
        ITournamentManager manager = new SingleElimTournamentManager
                .SingleElimTournamentManagerBuilder().build();
        LinkedHashMap<String, IPlayer> playersInTurnOrder = new LinkedHashMap<>();
        playersInTurnOrder.put("marley", new Player(new BuyNow()));
        TournamentResult actualResult = manager.runTournament(playersInTurnOrder);

        Set<String> ranking = new HashSet<>();
        ranking.add("marley");
        TournamentResult expectedResult = new TournamentResult(ranking, new HashSet<>());
        Assertions.assertTrue(tournamentResultsEquals(actualResult, expectedResult));
    }


    private boolean tournamentResultsEquals(TournamentResult result1, TournamentResult result2) {
        return result1.getWinners().equals(result2.getWinners())
                && result1.getCheaters().equals(result2.getCheaters());
    }
}
