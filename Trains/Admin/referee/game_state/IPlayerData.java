package referee.game_state;

import game_state.RailCard;
import java.util.Set;
import map.Destination;
import map.IRailConnection;

public interface IPlayerData {
  IPlayerHand<RailCard> getPlayerHand();
  int getNumRails();
  void setNumRails(int numRails) throws IllegalArgumentException, IllegalStateException;
  Set<Destination> getDestinations();
  Set<IRailConnection> getOwnedConnections();
  IPlayerData copyData();
}
