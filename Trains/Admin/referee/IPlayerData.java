package referee;

import game_state.RailCard;
import java.util.Set;
import map.Destination;
import map.IRailConnection;

public interface IPlayerData {
  String getPlayerID();
  IPlayerHand<RailCard> getPlayerHand();
  int getNumRails();
  void setNumRails(int numRails) throws IllegalArgumentException, IllegalStateException;
  Set<Destination> getDestinations();
  Set<IRailConnection> getOwnedConnections();
  IPlayerData copyData();
}
