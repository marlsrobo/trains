import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static junit.framework.TestCase.assertEquals;

/**
 * Resource: https://stackoverflow.com/questions/1119385/junit-test-for-system-out-println
 * Class for testing the xjson main method
 */
public class xjsonTests {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setIn(System.in);
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    public void testNumber() {
        System.setIn(new ByteArrayInputStream("1".getBytes()));
        Xjson.main(new String[]{});
        assertEquals("-1\n", outContent.toString());
    }

    @Test
    public void testString() {
        System.setIn(new ByteArrayInputStream("hello".getBytes()));
        Xjson.main(new String[]{});
        assertEquals("\"olleh\"\n", outContent.toString());
    }

    @Test
    public void testFalse() {
        System.setIn(new ByteArrayInputStream("false".getBytes()));
        Xjson.main(new String[]{});
        assertEquals("true\n", outContent.toString());
    }

    @Test
    public void testTrue() {
        System.setIn(new ByteArrayInputStream("true".getBytes()));
        Xjson.main(new String[]{});
        assertEquals("false\n", outContent.toString());
    }

    @Test
    public void testNull() {
        System.setIn(new ByteArrayInputStream("null".getBytes()));
        Xjson.main(new String[]{});
        assertEquals("null\n", outContent.toString());
    }

    @Test
    public void testSimpleObject() {
        System.setIn(new ByteArrayInputStream("{\"hello\" : 1}".getBytes()));
        Xjson.main(new String[]{});
        assertEquals("{\"hello\":-1}\n", outContent.toString());
    }

    @Test
    public void testArray() {
        System.setIn(new ByteArrayInputStream("[true, \"Laura\", -42.5, null]".getBytes()));
        Xjson.main(new String[]{});
        assertEquals("[null,42.5,\"aruaL\",false]\n", outContent.toString());
    }

    @Test
    public void testJsonSeries() {
        System.setIn(new ByteArrayInputStream("[true, \"Laura\", -42.5, null]\"hello\"\n\n\t12".getBytes()));
        Xjson.main(new String[]{});
        assertEquals("[null,42.5,\"aruaL\",false]\n\"olleh\"\n-12\n", outContent.toString());
    }

    @Test
    public void testBiggerObject() {
        System.setIn(new ByteArrayInputStream(("{\"name\": \"Marley\"," +
                "\"address\": {\"street\": \"MainStreet\"," +
                "\"city\": \"Boston\"," +
                "\"zip\": 02115}," +
                "\"courses\" : [\"cs4500\", \"cs3200\", \"ds3000\"]," +
                "\"enrolled\" : true}").getBytes()));
        Xjson.main(new String[]{});
        assertEquals("{\"name\":\"yelraM\"," +
                "\"address\":{\"street\":\"teertSniaM\"," +
                "\"city\":\"notsoB\"," +
                "\"zip\":\"51120\"}," +
                "\"courses\":[\"0003sd\",\"0023sc\",\"0054sc\"]," +
                "\"enrolled\":false}\n", outContent.toString());
    }
}