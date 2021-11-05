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
 * Class for testing the xhead main method
 */
public class xheadTest {

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
  public void test1() {
    System.setIn(new ByteArrayInputStream("hello\nbye\n".getBytes()));
    Xhead.main(new String[]{"-1"});
    assertEquals("hello\n", outContent.toString());
  }

  @Test
  public void test2() {
    System.setIn(new ByteArrayInputStream("hello\nbye\n".getBytes()));
    Xhead.main(new String[]{"-2"});
    assertEquals("hello\nbye\n", outContent.toString());
  }

  @Test
  public void test3MoreLines() {
    System.setIn(new ByteArrayInputStream("hello\nbye\n".getBytes()));
    Xhead.main(new String[]{"-3"});
    assertEquals("hello\nbye\n", outContent.toString());
  }

  @Test
  public void testMalformedNoDash() {
    System.setIn(new ByteArrayInputStream("hello\nbye\n".getBytes()));
    Xhead.main(new String[]{"2"});
    assertEquals("error\n", outContent.toString());
  }

  @Test
  public void testMalformedNotIntNoDash() {
    System.setIn(new ByteArrayInputStream("hello\nbye\n".getBytes()));
    Xhead.main(new String[]{"test"});
    assertEquals("error\n", outContent.toString());
  }

  @Test
  public void testMalformedTwoArgs() {
    System.setIn(new ByteArrayInputStream("hello\nbye\n".getBytes()));
    Xhead.main(new String[]{"-2 hi"});
    assertEquals("error\n", outContent.toString());
  }

  @Test
  public void testMalformedNegativeInt() {
    System.setIn(new ByteArrayInputStream("hello\nbye\n".getBytes()));
    Xhead.main(new String[]{"--10"});
    assertEquals("error\n", outContent.toString());
  }

  @Test
  public void testMalformedNoArgs() {
    System.setIn(new ByteArrayInputStream("hello\nbye\n".getBytes()));
    Xhead.main(new String[]{""});
    assertEquals("error\n", outContent.toString());
  }

  @Test
  public void testMalformedDecimal() {
    System.setIn(new ByteArrayInputStream("hello\nbye\n".getBytes()));
    Xhead.main(new String[]{"-2.5"});
    assertEquals("error\n", outContent.toString());
  }

  @Test
  public void testMalformedTextWithDash() {
    System.setIn(new ByteArrayInputStream("hello\nbye\n".getBytes()));
    Xhead.main(new String[]{"-hello"});
    assertEquals("error\n", outContent.toString());
  }

}
