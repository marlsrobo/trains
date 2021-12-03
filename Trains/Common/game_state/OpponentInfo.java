package game_state;

import java.util.HashSet;
import java.util.Set;
import map.IRailConnection;
import utils.UnorderedPair;

public class OpponentInfo implements IOpponentInfo {
  private Set<IRailConnection> ownedConnections;

  public OpponentInfo(Set<IRailConnection> ownedConnections) {
    this.ownedConnections = new HashSet<>(ownedConnections);
  }

  @Override
  public Set<IRailConnection> getOwnedConnections() {
    return new HashSet<>(this.ownedConnections);
  }

  @Override
  public String toString() {
    return this.ownedConnections.toString();
  }

  @Override
  public int hashCode() {
    return this.ownedConnections.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof OpponentInfo)) {
      return false;
    }

    OpponentInfo otherOpponentInfo = (OpponentInfo) obj;

    return this.ownedConnections.equals(otherOpponentInfo.ownedConnections);

  }
}
