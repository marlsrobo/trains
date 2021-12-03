package remote;

import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeoutException;
import player.IPlayer;

/**
 * Runs one client for a remote game of Trains. It first connects to the server on the specified
 * host and port, sends its given name, and then waits for commands from the server.
 */
public class Client implements Runnable {

    private final String serverHost;
    private final int serverPort;
    private final String name;
    private final IPlayer player;

    public Client(String serverHost, int serverPort, String name, IPlayer player) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.name = name;
        this.player = player;
    }

    /**
     * Runs the client according to the inputs to its constructor.
     *
     * Follows this order:
     * 1. Connect to the server
     * 2. Send its name to the server
     * 3. wait for commands from the server
     *
     * This will eventually timeout after not hearing from the server for a certain amount of time.
     */
    @Override
    public void run() {
        try {
            Socket serverConnection = new Socket(this.serverHost, this.serverPort);
            // Send this players name to the server
            PrintWriter writer = new PrintWriter(serverConnection.getOutputStream());
            writer.print(new JsonPrimitive(this.name));
            writer.flush();

            // The proxy server will read all further communication from the server, and translate
            // into method calls on the player
            ProxyServer proxyServer = new ProxyServer(serverConnection, this.player);
            proxyServer.run();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
