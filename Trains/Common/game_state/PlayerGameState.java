package game_state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import map.IRailConnection;
import map.ITrainMap;
import referee.game_state.IPlayerData;

public class PlayerGameState implements IPlayerGameState {
  private final Set<IRailConnection> ownedConnections;
  private final Map<RailCard, Integer> cardsInHand;
  private final int numRails;
  private final List<IOpponentInfo> opponentInfo;

  public PlayerGameState(IPlayerData playerData, List<IOpponentInfo> opponentInfo) {
    Objects.requireNonNull(playerData);
    Objects.requireNonNull(opponentInfo);

    this.ownedConnections = new HashSet<>(playerData.getOwnedConnections());
    this.cardsInHand = new HashMap<>(playerData.getPlayerHand().getHand());
    this.numRails = playerData.getNumRails();
    this.opponentInfo = new ArrayList<>(opponentInfo);
  }

  @Override
  public Set<IRailConnection> getOwnedConnections() {
    return new HashSet<>(this.ownedConnections);
  }

  @Override
  public Set<IRailConnection> calculateUnoccupiedConnections(ITrainMap map) {
    Set<IRailConnection> unoccupiedConnections = map.getRailConnections();

    unoccupiedConnections.removeAll(this.ownedConnections);
    for (IOpponentInfo oneOpponentInfo : this.opponentInfo) {
      unoccupiedConnections.removeAll(oneOpponentInfo.getOwnedConnections());
    }

    return unoccupiedConnections;
  }

  @Override
  public Map<RailCard, Integer> getCardsInHand() {
    return new HashMap<>(this.cardsInHand);
  }

  @Override
  public int getNumRails() {
    return this.numRails;
  }

  @Override
  public List<IOpponentInfo> getOpponentInfo() {
    return new ArrayList<>(this.opponentInfo);
  }
}