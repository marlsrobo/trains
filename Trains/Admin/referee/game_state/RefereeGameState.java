package referee.game_state;

import game_state.IOpponentInfo;
import game_state.IPlayerGameState;
import game_state.OpponentInfo;
import game_state.PlayerGameState;
import game_state.RailCard;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import map.IRailConnection;
import map.ITrainMap;
import referee.ActionChecker;
import referee.ScoreCalculator;
import utils.RailCardUtils;

public class RefereeGameState implements IRefereeGameState {

    private final ITrainMap trainMap;
    private final List<RailCard> deck;
    private int indexOfCurrentPlayer;
    private final List<IPlayerData> playerDataInTurnOrder;

    public RefereeGameState(
        List<IPlayerData> playerDataInTurnOrder, List<RailCard> deck, ITrainMap map) {
        Objects.requireNonNull(playerDataInTurnOrder);
        Objects.requireNonNull(deck);
        Objects.requireNonNull(map);

        this.playerDataInTurnOrder = copyPlayerData(playerDataInTurnOrder);
        this.deck = new ArrayList<>(deck);
        this.trainMap = map;
        this.indexOfCurrentPlayer = 0;
    }

    // TODO: crete new PlayerData instead of using copyData() in order to enforce the implementation
    private static List<IPlayerData> copyPlayerData(List<IPlayerData> playerDataInTurnOrder) {
        List<IPlayerData> result = new ArrayList<>();
        for (IPlayerData playerData : playerDataInTurnOrder) {
            result.add(playerData.copyData());
        }
        return result;
    }

    @Override
    public ActionChecker getActionChecker() {
        return new ActionChecker();
    }

    /**
     * Returns the playerData of the currently active player.
     *
     * @return player data of the currently active player
     */
    private IPlayerData getActivePlayer() {
        return this.playerDataInTurnOrder.get(this.indexOfCurrentPlayer);
    }

    @Override
    public void advanceTurn() {
        this.indexOfCurrentPlayer =
            (this.indexOfCurrentPlayer + 1) % this.playerDataInTurnOrder.size();
    }

    @Override
    public void removeActivePlayer() {
        // Because a player's rails, cards, destinations, and connections are calculated
        // from the playerData, this removal automatically discards/removes those things as well
        this.playerDataInTurnOrder.remove(this.indexOfCurrentPlayer);
        if (this.playerDataInTurnOrder.size() != 0) {
            this.indexOfCurrentPlayer %= this.playerDataInTurnOrder.size();
        }
    }

    @Override
    public IPlayerGameState getActivePlayerState() {
        return new PlayerGameState(this.getActivePlayer(), this.calculateOpponentInfo());
    }

    private List<IOpponentInfo> calculateOpponentInfo() {
        List<IOpponentInfo> result = new ArrayList<>();
        for (int index = 0; index < this.playerDataInTurnOrder.size(); index += 1) {
            if (index != this.indexOfCurrentPlayer) {
                result.add(
                    new OpponentInfo(this.playerDataInTurnOrder.get(index).getOwnedConnections()));
            }
        }
        return result;
    }

    /**
     * If numCards is greater than the number remaining in the deck, this method will draw all
     * remaining cards.
     *
     * @param numCards The number of cards to draw.
     * @return a copy of the cards given to the player in the order in which they were drawn.
     * @throws IllegalArgumentException if numCards is negative.
     */
    @Override
    public List<RailCard> drawCardsForActivePlayer(int numCards) throws IllegalArgumentException {
        if (numCards < 1) {
            throw new IllegalArgumentException("Must draw positive number of cards.");
        }
        if (numCards > this.deck.size()) {
            numCards = this.deck.size();
        }

        IPlayerHand<RailCard> activePlayerHand = getActivePlayer().getPlayerHand();
        List<RailCard> drawnCards = new ArrayList<>();
        for (int ii = 0; ii < numCards; ii++) {
            RailCard oneCard = this.deck.remove(0);
            drawnCards.add(oneCard);
            activePlayerHand.addCardsToHand(oneCard, 1);
        }

        return drawnCards;
    }

    @Override
    public boolean acquireConnectionForActivePlayer(IRailConnection desiredConnection)
        throws IllegalArgumentException {
        if (!this.getActionChecker()
            .canAcquireConnection(this.getActivePlayerState(), this.trainMap, desiredConnection)) {
            return false;
        } else {
            IPlayerData playerData = this.getActivePlayer();
            // Remove rails and cards from player's hand
            playerData.setNumRails(playerData.getNumRails() - desiredConnection.getLength());
            playerData
                .getPlayerHand()
                .removeCardsFromHand(
                    RailCardUtils.railCardFromColor(desiredConnection.getColor()),
                    desiredConnection.getLength());
            // Add connection to player's list of connections
            playerData.getOwnedConnections().add(desiredConnection);
            return true;
        }
    }

    @Override
    public List<Integer> calculatePlayerScores() {
        return new ScoreCalculator().scorePlayers(new ArrayList<>(this.playerDataInTurnOrder));
    }
}