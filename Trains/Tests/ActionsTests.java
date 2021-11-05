import Other.City;
import Other.Connection;
import Other.ConnectionColor;
import Other.Action.AcquiredAction;
import Other.Action.Action;
import Other.Action.MoreCardsAction;
import org.junit.Test;

import static junit.framework.TestCase.*;


public class ActionsTests {

    @Test
    public void testTypesOfActions() {
        Action action1 = new MoreCardsAction();
        Action action2 = new AcquiredAction(new Connection(ConnectionColor.red, 5,
                new City("boston", 0, 1), new City("nyc", 0.8, 0.2)));

        assertTrue(action1.getValue() instanceof String);
        assertTrue(action2.getValue() instanceof Connection);

        String value = action1.getValue();
        Connection connection = action2.getValue();
    }
}
