package remote;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import player.IPlayer;

public class ProxyServer {

    private static final int DISCONNECT_TIMEOUT_MILLIS = 20000;

    private final Reader input;
    private final PrintWriter output;
    private final IPlayer player;

    public ProxyServer(Socket server, IPlayer player) throws IOException {
        this.input = new InputStreamReader(server.getInputStream());
        this.output = new PrintWriter(server.getOutputStream(), true);
        this.player = player;
    }

    public void run() {
        JsonStreamParser parser = new JsonStreamParser(this.input);
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime > DISCONNECT_TIMEOUT_MILLIS) {
            while (!parser.hasNext()) {
                // Do a better synchronization
            }

            JsonElement element = parser.next();
            JsonObject methodInfo = element.getAsJsonObject();
        }
    }
}

/**
 *
 *
 {
    "name" : "setup",
    "args" : ["ITrainMap", "int", "List<RailCard>"]
 }

 *
 *
 */
