package remote;

import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeoutException;
import player.IPlayer;

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

    @Override
    public void run() {
        try {
            Socket serverConnection = new Socket(this.serverHost, this.serverPort);
            // Send this players name to the server
            new PrintWriter(serverConnection.getOutputStream()).print(new JsonPrimitive(this.name));

            // The proxy server will read all further communication from the server, and translate
            // into method calls on the player
            ProxyServer proxyServer = new ProxyServer(serverConnection, this.player);
            proxyServer.run();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
