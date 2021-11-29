package remote;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import player.IPlayer;

public class Client implements Runnable {

    private final String serverHost;
    private final int serverPort;
    private final String name;
    private final IPlayer player;

    public Client(String serverHost, int serverPort, String name, IPlayer player) throws IOException {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.name = name;
        this.player = player;
    }

    @Override
    public void run() {
        try {
            Socket serverConnection = new Socket(this.serverHost, this.serverPort);
            new PrintWriter(serverConnection.getOutputStream()).print(this.name);

            ProxyServer proxyServer = new ProxyServer(serverConnection, this.player);
            proxyServer.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
