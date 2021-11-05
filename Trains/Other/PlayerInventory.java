package Other;


import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Class for the private inventory of a Player in the game of Trains.
 */
public class PlayerInventory {
    private final Map<ConnectionColor, Integer> cardsInHand;
    private final int railsInHand;
    private final Set<Destination> destinations;

    public final static int PLAYER_DESTINATIONS = 2;

    /**
     * Cosntructor for a PlayerInventory
     * @param cardsInHand the Map of ConnectionColor to Integer specifying the number of colored cards of each color.
     * @param railsInHand the number of rails for this player
     * @param destinations the chosen destinations of the player
     */
    public PlayerInventory(Map<ConnectionColor, Integer> cardsInHand, int railsInHand, Set<Destination> destinations) {
        this.cardsInHand = setCardsInHand(cardsInHand);
        if (railsInHand < 0) {
            throw new IllegalArgumentException("Player can't have negative rails");
        }
        this.railsInHand = railsInHand;
        this.destinations = destinations;
    }

    public PlayerInventory(Map<ConnectionColor, Integer> cards, int rails) {
        this(cards, rails, new HashSet<>());
    }

    /**
     * Throws any exceptions if the cardsInHand for the PlayerInventory is invalid
     * @param cardsInHand the colored cards of this player
     * @return the cards if they are valid, else throws exceptions
     */
    private Map<ConnectionColor, Integer> setCardsInHand(Map<ConnectionColor, Integer> cardsInHand) {
        if (!new HashSet<>(Arrays.asList(ConnectionColor.values())).equals(cardsInHand.keySet())) {
            throw new IllegalArgumentException("Players' card hands must contain all possible " +
                    "colors of colored cards");
        }
        for (Integer numCards : cardsInHand.values()) {
            if (numCards < 0) {
                throw new IllegalArgumentException("Cannot have a negative number of colored cards " +
                        "for a player");
            }
        }
        return cardsInHand;
    }

    /**
     * Getter for cards
     * @return Map of color of the card to number of that card
     */
    public Map<ConnectionColor, Integer> getCardsInHand() {
        return new HashMap<>(cardsInHand);
    }

    /**
     * Getter for rails
     * @return int number of rails
     */
    public int getRailsInHand() {
        return railsInHand;
    }

    /**
     * Getter for destinations
     * @return a Set of Destinations
     */
    public Set<Destination> getDestinations() {
        return new HashSet<>(destinations);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerInventory inventory = (PlayerInventory) o;
        return railsInHand == inventory.railsInHand
                && cardsInHand.equals(inventory.cardsInHand)
                && destinations.equals(inventory.destinations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardsInHand, railsInHand, destinations);
    }
}
