import game_state.IPlayerGameState;
import game_state.RailCard;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import map.Destination;
import map.ITrainMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import player.IPlayer;
import player.Player;
import referee.game_state.IPlayerHand;
import referee.game_state.TrainsPlayerHand;
import strategy.BuyNow;
import strategy.Hold10;
import strategy.IStrategy;
import strategy.TestStrategy;

public class TestPlayer {

    String buyNowPath = "out/production/mark-twain/strategy/BuyNow.class";
    String hold10Path = "out/production/mark-twain/strategy/Hold10.class";
    IPlayer hold10FromStrategy;
    IPlayer buyNowFromStrategy;
    IPlayer hold10FromFile;
    IPlayer buyNowFromFile;

    @BeforeEach
    public void init() {
        this.hold10FromStrategy = new Player(new Hold10());
        this.buyNowFromStrategy = new Player(new BuyNow());
        this.buyNowFromFile = new Player(buyNowPath);
        this.hold10FromFile = new Player(hold10Path);
    }

    @Test
    public void TestPlayers() throws TimeoutException {
        // None of these boring methods should throw exceptions, etc
        TestBoringPlayerMethods(hold10FromStrategy);
        TestBoringPlayerMethods(hold10FromFile);
        TestBoringPlayerMethods(buyNowFromStrategy);
        TestBoringPlayerMethods(buyNowFromFile);
    }

    public void TestBoringPlayerMethods(IPlayer player) throws TimeoutException {
        player.winNotification(true);
        player.winNotification(false);
        player.receiveCards(null);
        player.receiveCards(Arrays.asList(RailCard.BLUE, RailCard.WHITE));
    }

    @Test
    public void TestSetUp() throws TimeoutException {
        // Test what is passed to strategy in case a player is not set up
        IPlayer p = new Player(new MockStrategy(new HashSet<>(), 2, null, 0, null));
        p.chooseDestinations(new HashSet<>());

        // Test that set-up properly stores and passes information to the strategy
        IPlayerHand<RailCard> sampleHand = new TrainsPlayerHand(new ArrayList<>());
        sampleHand.addCardsToHand(RailCard.BLUE, 2);
        sampleHand.addCardsToHand(RailCard.RED, 1);
        p =
            new Player(
                new MockStrategy(
                    new HashSet<>(),
                    2,
                    TestMapRenderer.readAndParseTestMap("bos-sea-tex.json"),
                    7,
                    sampleHand.getHand()));
        p.setup(
            TestMapRenderer.readAndParseTestMap("bos-sea-tex.json"),
            7,
            Arrays.asList(RailCard.BLUE, RailCard.RED, RailCard.BLUE));
        p.chooseDestinations(new HashSet<>());
        p.takeTurn(null);
    }


    @Test
    public void TestPlayerIntegrations() throws TimeoutException {
        TestIntegration(this.buyNowFromFile, new BuyNow());
        TestIntegration(this.buyNowFromStrategy, new BuyNow());

        TestIntegration(this.hold10FromFile, new Hold10());
        TestIntegration(this.hold10FromStrategy, new Hold10());
    }

    private void TestIntegration(IPlayer player, IStrategy strategy) throws TimeoutException {
        ITrainMap map = TestStrategy.readAndParseTestMap("bos-sea-red-white.json").getFirst();
        IPlayerGameState gameState =
            TestStrategy.readAndParseTestMap("bos-sea-red-white.json").getSecond();
        TestStrategy s = new TestStrategy();
        s.init();

        player.setup(map, 0, new ArrayList<>());
        Set<Destination> chosenDestinations = player.chooseDestinations(s.destinations);
        Set<Destination> notChosenDestinations = new HashSet<>(s.destinations);
        notChosenDestinations.removeAll(chosenDestinations);
        Assertions.assertEquals(
            strategy.chooseDestinations(s.destinations, 2, map, 0, null), notChosenDestinations);

//    Assertions.assertEquals(
//        strategy.takeTurn(gameState, map, chosenDestinations).getActionType(),
//        player.takeTurn(gameState).getActionType());
//    Assertions.assertEquals(
//        strategy.takeTurn(gameState, map, chosenDestinations).getRailConnection().orElse(null),
//        player.takeTurn(gameState).getRailConnection().orElse(null));
    }
}
