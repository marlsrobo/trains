package game_state;

import java.util.Set;
import map.IRailConnection;

public interface IOpponentInfo {
  Set<IRailConnection> getOwnedConnections();
}
