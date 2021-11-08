
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Objects;
import org.junit.jupiter.api.Test;
import utils.OrderedPair;
import utils.UnorderedPair;

public class TestOrderedPair {

    /**
     * Tests various invalid and valid constructions of UnorderedPair
     */
    @Test
    public void testConstructors() {
        try {
            OrderedPair<Integer> p = new OrderedPair<>(null, 1);
            fail();
        } catch (NullPointerException ignored) {
        }
        try {
            OrderedPair<Integer> p = new OrderedPair<>(1, null);
            fail();
        } catch (NullPointerException ignored) {
        }
        try {
            OrderedPair<Integer> p = new OrderedPair<>(null);
            fail();
        } catch (NullPointerException ignored) {
        }
        try {
            OrderedPair<Integer> p = new OrderedPair<>(new OrderedPair<Integer>(null, 1));
            fail();
        } catch (NullPointerException ignored) {
        }
        OrderedPair<Integer> success1 = new OrderedPair<>(2, 3);
        OrderedPair<Integer> success2 = new OrderedPair<>(success1);
    }

    /**
     * Tests that if utils.OrderedPair elements are mutated, the references are carried to clones.
     */
    @Test
    public void testMutability() {
        OrderedPair<StringBuilder> mutable = new OrderedPair<>(new StringBuilder(), new StringBuilder());
        OrderedPair<StringBuilder> cloneOfMutable = new OrderedPair<>(mutable);
        mutable.first.append("mutation");
        assertEquals(mutable.first.toString(), cloneOfMutable.first.toString());
    }

    @Test
    public void testHashCode() {
        String a = "a";
        String b = "b";
        OrderedPair<String> pair = new OrderedPair<>(a, b);
        OrderedPair<String> reversedPair = new OrderedPair<>(b, a);
        // Equality means equality of hashcode
        assertNotEquals(pair.hashCode(), reversedPair.hashCode());
        // Hashcode from the element hashcodes
        assertEquals(pair.hashCode(), Objects.hash(a,b));
    }

    @Test
    public void testEquals() {
        String a = "a";
        String b = "b";
        OrderedPair<String> pair = new OrderedPair<>(a, b);
        OrderedPair<String> samePair = new OrderedPair<>("a", "b");
        OrderedPair<String> reversedPair = new OrderedPair<>(b, a);
        assertEquals(pair, pair);
        // Tests that .equals is used on elements
        assertEquals(pair, samePair);
        // Order matters
        assertNotEquals(pair, reversedPair);
        // Null and other types don't break
        assertNotEquals(null, pair);
        assertNotEquals(new UnorderedPair<>(a, b), pair);
    }

    @Test
    public void testReverse() {
        String a = "a";
        OrderedPair<String> pair = new OrderedPair<>(a, "b");
        OrderedPair<String> reversedPair = new OrderedPair<>("b", a);
        assertEquals(pair.reverse(), reversedPair);
        assertEquals(pair.reverse().reverse(), pair);

        // Reverse gives a new OrderedPair
        assertNotSame(pair, pair.reverse().reverse());

        // But still has references to original elements
        assertSame(pair.first, pair.reverse().reverse().first);
    }
}
