package tournament_manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import player.IPlayer;

/**
 * Utility class that is used to allocate players into games for one round of a tournament.
 */
public class PlayerAllocator {

    /**
     * Allocates the given players into a list of games for one round of a tournament.
     * <p>
     * Attempts to allocate the maximum number of players to each game, and any leftover players are
     * grouped together. If the leftover players are less than the minimum for one game, takes one
     * players from each game until there are enough players for one more game.
     *
     * @return the players allocated into games. Each item in the list represents a single game's
     * players.
     * @throws IllegalStateException    if there is no valid allocation of the given players into
     *                                  games that respect the given minimum and maximum players per
     *                                  game.
     * @throws IllegalArgumentException if minPlayersPerGame is greater than maxPlayersPerGame.
     */
    public static List<LinkedHashMap<String, IPlayer>> allocatePlayersToGames(
        LinkedHashMap<String, IPlayer> playersToAllocate,
        int maxPlayersPerGame,
        int minPlayersPerGame) {
        if (minPlayersPerGame > maxPlayersPerGame) {
            throw new IllegalArgumentException(
                "Min players per game cannot be greater than max players per game");
        }
        try {
            return allocatePlayersAcc(playersToAllocate, maxPlayersPerGame, minPlayersPerGame,
                true);
        } catch (NotEnoughPlayersException e) {
            throw new IllegalStateException("Min and max players per game are invalid");
        }
    }

    /**
     * Helper function to allocate players to games.
     *
     * @param playersToAllocate All players that have not yet been allocated to games.
     * @param playersPerGame    The number of players to allocate to each game.
     * @param minPlayersPerGame The minimum number of players to allocate to one game.
     * @param attemptRecovery   Whether this method should attempt to remove players from full games
     *                          to make a valid allocation.
     * @return the players allocated into games. Each item in the list represents a single game's *
     * players.
     * @throws NotEnoughPlayersException if there is no valid allocation of the given players into
     *                                   games that respect the given minimum and maximum players
     *                                   per game, or leftover players is less than
     *                                   minPlayersPerGame and attemptRecovery is false.
     */
    private static List<LinkedHashMap<String, IPlayer>> allocatePlayersAcc(
        LinkedHashMap<String, IPlayer> playersToAllocate,
        int playersPerGame,
        int minPlayersPerGame,
        boolean attemptRecovery) throws NotEnoughPlayersException {

        if (playersToAllocate.size() == 0) {
            return new ArrayList<>();
        }
        if (playersToAllocate.size() < minPlayersPerGame) {
            throw new NotEnoughPlayersException();
        }

        List<LinkedHashMap<String, IPlayer>> result;
        try {
            LinkedHashMap<String, IPlayer> playersForThisGame = allocateOneGame(playersToAllocate,
                playersPerGame);
            LinkedHashMap<String, IPlayer> remainingPlayers = new LinkedHashMap<>(
                playersToAllocate);
            remainingPlayers.keySet().removeAll(playersForThisGame.keySet());

            result = allocatePlayersAcc(remainingPlayers, playersPerGame, minPlayersPerGame,
                attemptRecovery);
            result.add(playersForThisGame);
        } catch (NotEnoughPlayersException e) {
            if (!attemptRecovery) {
                throw e;
            }
            result = allocatePlayersAcc(playersToAllocate, playersPerGame - 1, minPlayersPerGame,
                false);
        }
        return result;
    }

    /**
     * Allocates up to playersPerGame players from playersToAllocate to one game.
     *
     * @param playersToAllocate All players that have not yet been allocated to games.
     * @param playersPerGame    The number of players to allocate to this game.
     * @return The players that were allocated to one game.
     */
    private static LinkedHashMap<String, IPlayer> allocateOneGame(
        LinkedHashMap<String, IPlayer> playersToAllocate, int playersPerGame) {

        LinkedHashMap<String, IPlayer> playersForThisGame = new LinkedHashMap<>();
        Iterator<Entry<String, IPlayer>> remainingPlayersInOrder = playersToAllocate.entrySet()
            .iterator();

        for (int i = 0; i < playersPerGame && remainingPlayersInOrder.hasNext(); i++) {
            Entry<String, IPlayer> nextPlayer = remainingPlayersInOrder.next();
            playersForThisGame.put(nextPlayer.getKey(), nextPlayer.getValue());
        }
        return playersForThisGame;
    }

    /**
     * Used internally to signal that there is no way to allocate the given players into games.
     */
    private static class NotEnoughPlayersException extends Exception {

    }
}
