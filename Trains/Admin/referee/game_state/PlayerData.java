package referee.game_state;

import game_state.RailCard;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import map.Destination;
import map.IRailConnection;

public class PlayerData implements IPlayerData {

    private final IPlayerHand<RailCard> hand;
    private int numRails;
    private final Set<Destination> destinations;
    private final Set<IRailConnection> ownedConnections;

    public PlayerData(IPlayerHand<RailCard> hand, int numRails,
        Set<Destination> destinations, Set<IRailConnection> ownedConnections) {
        Objects.requireNonNull(hand);
        Objects.requireNonNull(destinations);
        Objects.requireNonNull(ownedConnections);

        this.hand = hand;
        this.numRails = numRails;
        this.destinations = destinations;
        this.ownedConnections = ownedConnections;
    }

    @Override
    public IPlayerHand<RailCard> getPlayerHand() {
        return this.hand;
    }

    @Override
    public int getNumRails() {
        return this.numRails;
    }

    @Override
    public void setNumRails(int numRails) throws IllegalArgumentException {
        if (numRails < 0) {
            throw new IllegalArgumentException("The number of rails must be set to a natural number");
        }
        this.numRails = numRails;
    }

    @Override
    public Set<Destination> getDestinations() {
        return this.destinations;
    }

    @Override
    public Set<IRailConnection> getOwnedConnections() {
        return this.ownedConnections;
    }

    @Override
    public IPlayerData copyData() {
        return new PlayerData(new TrainsPlayerHand(this.hand.getHand()),
            this.numRails, new HashSet<>(this.destinations), new HashSet<>(this.ownedConnections));
    }
}
