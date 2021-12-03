import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.Test;
import remote.Server.ClientHandler;
import utils.SynchronizedCounter;

public class TestServer {

    @Test
    public void testClientHandler() {
        SynchronizedCounter counter = new SynchronizedCounter();
        ClientHandler handler = new ClientHandler(new ByteArrayInputStream("\"name\"".getBytes()), counter);

        handler.run();

        assertEquals(1, counter.get());
        assertTrue(handler.getRespondedInTime());
        assertEquals("name", handler.getName());
    }

    @Test
    public void testClientHandlerNoName() {
        SynchronizedCounter counter = new SynchronizedCounter();
        ClientHandler handler = new ClientHandler(new ByteArrayInputStream("".getBytes()), counter);

        handler.run();

        assertEquals(0, counter.get());
        assertFalse(handler.getRespondedInTime());
    }
}
