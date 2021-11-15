import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import player.IPlayer;
import player.Player;
import strategy.Hold10;
import tournament_manager.PlayerAllocator;

public class TestPlayerAllocator {

    @Test
    public void testAllocate17PlayersMin3Max8() {
        List<LinkedHashMap<String, IPlayer>> expectedAllocation = new ArrayList<>();
        LinkedHashMap<String, IPlayer> allPlayers = new LinkedHashMap<>();

        LinkedHashMap<String, IPlayer> game1 = makeOneGame(0, 7);
        LinkedHashMap<String, IPlayer> game2 = makeOneGame(7, 7);
        LinkedHashMap<String, IPlayer> game3 = makeOneGame(14, 3);
        allPlayers.putAll(game1);
        allPlayers.putAll(game2);
        allPlayers.putAll(game3);
        expectedAllocation.add(game3);
        expectedAllocation.add(game2);
        expectedAllocation.add(game1);

        assertEquals(expectedAllocation, PlayerAllocator.allocatePlayersToGames(allPlayers, 8, 3));
    }

    @Test
    public void testAllocate17PlayersMin2Max8() {
        List<LinkedHashMap<String, IPlayer>> expectedAllocation = new ArrayList<>();
        LinkedHashMap<String, IPlayer> allPlayers = new LinkedHashMap<>();

        LinkedHashMap<String, IPlayer> game1 = makeOneGame(0, 8);
        LinkedHashMap<String, IPlayer> game2 = makeOneGame(8, 7);
        LinkedHashMap<String, IPlayer> game3 = makeOneGame(15, 2);
        allPlayers.putAll(game1);
        allPlayers.putAll(game2);
        allPlayers.putAll(game3);
        expectedAllocation.add(game3);
        expectedAllocation.add(game2);
        expectedAllocation.add(game1);

        assertEquals(expectedAllocation, PlayerAllocator.allocatePlayersToGames(allPlayers, 8, 2));
    }

    @Test
    public void testAllocate2PlayersMin2Max8() {
        List<LinkedHashMap<String, IPlayer>> expectedAllocation = new ArrayList<>();
        LinkedHashMap<String, IPlayer> allPlayers = new LinkedHashMap<>();

        LinkedHashMap<String, IPlayer> game1 = makeOneGame(0, 2);
        allPlayers.putAll(game1);
        expectedAllocation.add(game1);

        assertEquals(expectedAllocation, PlayerAllocator.allocatePlayersToGames(allPlayers, 8, 2));
    }

    @Test
    public void testAllocate16PlayersMin2Max8() {
        List<LinkedHashMap<String, IPlayer>> expectedAllocation = new ArrayList<>();
        LinkedHashMap<String, IPlayer> allPlayers = new LinkedHashMap<>();

        LinkedHashMap<String, IPlayer> game1 = makeOneGame(0, 8);
        LinkedHashMap<String, IPlayer> game2 = makeOneGame(8, 8);
        allPlayers.putAll(game1);
        allPlayers.putAll(game2);
        expectedAllocation.add(game2);
        expectedAllocation.add(game1);

        assertEquals(expectedAllocation, PlayerAllocator.allocatePlayersToGames(allPlayers, 8, 2));
    }

    @Test
    public void testAllocateNotEnoughPlayers() {
        List<LinkedHashMap<String, IPlayer>> expectedAllocation = new ArrayList<>();
        LinkedHashMap<String, IPlayer> allPlayers = new LinkedHashMap<>();

        LinkedHashMap<String, IPlayer> game1 = makeOneGame(0, 1);
        allPlayers.putAll(game1);

        assertThrows(RuntimeException.class,
            () -> PlayerAllocator.allocatePlayersToGames(allPlayers, 8, 2));
    }

    @Test
    public void testAllocateInvalidMinMax() {
        List<LinkedHashMap<String, IPlayer>> expectedAllocation = new ArrayList<>();
        LinkedHashMap<String, IPlayer> allPlayers = new LinkedHashMap<>();

        LinkedHashMap<String, IPlayer> game1 = makeOneGame(0, 11);
        allPlayers.putAll(game1);

        assertThrows(RuntimeException.class,
            () -> PlayerAllocator.allocatePlayersToGames(allPlayers, 5, 4));
    }

    @Test
    public void testAllocateMinMaxEqual() {
        List<LinkedHashMap<String, IPlayer>> expectedAllocation = new ArrayList<>();
        LinkedHashMap<String, IPlayer> allPlayers = new LinkedHashMap<>();

        LinkedHashMap<String, IPlayer> game1 = makeOneGame(0, 8);
        LinkedHashMap<String, IPlayer> game2 = makeOneGame(8, 8);
        allPlayers.putAll(game1);
        allPlayers.putAll(game2);
        expectedAllocation.add(game2);
        expectedAllocation.add(game1);

        assertEquals(expectedAllocation, PlayerAllocator.allocatePlayersToGames(allPlayers, 8, 8));
    }

    private static LinkedHashMap<String, IPlayer> makeOneGame(int startingNum,
        int numPlayersToAdd) {
        LinkedHashMap<String, IPlayer> game = new LinkedHashMap<>();
        for (int ii = 0; ii < numPlayersToAdd; ii++) {
            IPlayer player = new Player(new Hold10());
            game.put("player" + (startingNum + ii), player);
        }
        return game;
    }
}
