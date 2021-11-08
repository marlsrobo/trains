package game_state;

import java.util.HashSet;
import java.util.Set;
import map.IRailConnection;

public class OpponentInfo implements IOpponentInfo {
  private Set<IRailConnection> ownedConnections;

  public OpponentInfo(Set<IRailConnection> ownedConnections) {
    this.ownedConnections = new HashSet<>(ownedConnections);
  }

  @Override
  public Set<IRailConnection> getOwnedConnections() {
    return new HashSet<>(this.ownedConnections);
  }
}
