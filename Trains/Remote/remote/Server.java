package remote;

import game_state.RailCard;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import map.Destination;
import map.ITrainMap;
import player.IPlayer;
import test_utils.TrainsMapUtils;
import tournament_manager.ITournamentManager;
import tournament_manager.SingleElimTournamentManager;
import tournament_manager.TournamentResult;

public class Server {

    private static final int NAME_TIMEOUT_MILLIS = 3000;
    private static final int ONE_CONNECTION_WAITING_TIMEOUT = 1000;
    private static final int PLAYERS_CONNECTION_WAITING_TIMEOUT = 20000;

    private final int port;

    private final Function<ITrainMap, List<Destination>> destinationProvider;
    private final Supplier<List<RailCard>> deckSupplier;
    private final Function<List<ITrainMap>, ITrainMap> mapSelector;

    public static class ServerBuilder {

        private final int port;
        private Function<ITrainMap, List<Destination>> destinationProvider;
        private Supplier<List<RailCard>> deckProvider;
        private Function<List<ITrainMap>, ITrainMap> mapSelector;

        /**
         * Construct the default builder for instances of Server.
         */
        public ServerBuilder(int port) {
            this.port = port;
            this.destinationProvider = TrainsMapUtils::defaultDestinationProvider;
            this.deckProvider = TrainsMapUtils::defaultDeckSupplier;
            this.mapSelector = TrainsMapUtils::defaultMapSelector;
        }

        /**
         * Sets the destination provider function that will be used in all games of this
         * tournament.
         * <p>
         * A destination provider function accepts an ITrainMap, and returns the order to hand the
         * destinations in that map to players.
         *
         * @param destinationProvider A valid destination provider function.
         * @return This builder modified to use the provided destination provider function.
         */
        public ServerBuilder destinationProvider(
            Function<ITrainMap, List<Destination>> destinationProvider) {
            this.destinationProvider = destinationProvider;
            return this;
        }

        /**
         * Sets the deck provider function that will be used in all games of this tournament.
         * <p>
         * A deck provider function accepts no arguments, and returns a list of RailCards in the
         * order that they should be provided to players when they draw from the deck.
         *
         * @param deckProvider A valid deck provider function.
         * @return This builder modified to use the provided deck provider function.
         */
        public ServerBuilder deckProvider(
            Supplier<List<RailCard>> deckProvider) {
            this.deckProvider = deckProvider;
            return this;
        }

        /**
         * Sets the map selector function that will be used by this tournament manager.
         * <p>
         * A map selector function accepts a non-empty list of ITrainMaps, and returns the one that
         * should be used for all games in this tournament.
         *
         * @param mapSelector A valid map selector function.
         * @return This builder modified to use the provided map selector function.
         */
        public ServerBuilder mapSelector(
            Function<List<ITrainMap>, ITrainMap> mapSelector) {
            this.mapSelector = mapSelector;
            return this;
        }

        /**
         * Constructs a SingleElimTournamentManager from the optional arguments given to this
         * builder.
         *
         * @return a SingleElimTournamentManager constructed with the given arguments.
         */
        public Server build() {
            Objects.requireNonNull(this.deckProvider);
            Objects.requireNonNull(this.destinationProvider);
            Objects.requireNonNull(this.mapSelector);

            return new Server(this.port, this.destinationProvider, this.deckProvider,
                this.mapSelector);
        }
    }

    private Server(int port,
        Function<ITrainMap, List<Destination>> destinationProvider,
        Supplier<List<RailCard>> deckSupplier,
        Function<List<ITrainMap>, ITrainMap> mapSelector) {
        this.port = port;
        this.destinationProvider = destinationProvider;
        this.deckSupplier = deckSupplier;
        this.mapSelector = mapSelector;
    }

    public TournamentResult run() throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(this.port);
        serverSocket.setSoTimeout(ONE_CONNECTION_WAITING_TIMEOUT);
        List<ClientHandler> signedUpPlayerHandlers = new ArrayList<>();
        List<Thread> signedUpPlayerThreads = new ArrayList<>();

        waitForPlayers(serverSocket, signedUpPlayerHandlers, signedUpPlayerThreads);

        if (signedUpPlayerHandlers.size() < 5) {
            waitForPlayers(serverSocket, signedUpPlayerHandlers, signedUpPlayerThreads);
        }

        LinkedHashMap<String, IPlayer> players = getSignedUpPlayers(signedUpPlayerHandlers,
            signedUpPlayerThreads);

        if (players.size() < 2) {
            return new TournamentResult(new HashSet<>(), new HashSet<>());
        }

        ITournamentManager manager = new SingleElimTournamentManager.SingleElimTournamentManagerBuilder()
            .deckProvider(this.deckSupplier)
            .destinationProvider(this.destinationProvider)
            .mapSelector(mapSelector)
            .build();

        return manager.runTournament(players);
    }

    private static LinkedHashMap<String, IPlayer> getSignedUpPlayers(
        List<ClientHandler> signedUpPlayerHandlers,
        List<Thread> signedUpPlayerThreads
    )
        throws IOException, InterruptedException {

        LinkedHashMap<String, IPlayer> players = new LinkedHashMap<>();
        for (int ii = 0; ii < signedUpPlayerHandlers.size(); ii++) {
            ClientHandler handler = signedUpPlayerHandlers.get(ii);
            Thread handlerThread = signedUpPlayerThreads.get(ii);
            handlerThread.join();

            if (handler.getRespondedInTime()) {
                players.put(handler.name, new ProxyPlayer(handler.getClientSocket()));
            }
        }

        return players;
    }

    private static void waitForPlayers(ServerSocket serverSocket,
        List<ClientHandler> signedUpPlayerHandlers, List<Thread> signedUpPlayerThreads)
        throws IOException {

        long startTime = System.currentTimeMillis();
        while (signedUpPlayerHandlers.size() < 50
            && System.currentTimeMillis() - startTime > PLAYERS_CONNECTION_WAITING_TIMEOUT) {

            Socket clientSocket = serverSocket.accept();
            ClientHandler handler = new ClientHandler(clientSocket);
            Thread clientHandlerThread = new Thread(handler);
            clientHandlerThread.start();
            signedUpPlayerHandlers.add(handler);
            signedUpPlayerThreads.add(clientHandlerThread);
        }
    }

    private static class ClientHandler implements Runnable {

        private final Socket clientSocket;
        private volatile String name;
        private volatile boolean respondedInTime = false;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            char[] name = new char[50];
            try {
                this.clientSocket.setSoTimeout(NAME_TIMEOUT_MILLIS);
                int numRead = new InputStreamReader(this.clientSocket.getInputStream()).read(name);

                if (numRead > 0) {
                    this.name = String.valueOf(name);
                    this.respondedInTime = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getName() {
            return this.name;
        }

        public boolean getRespondedInTime() {
            return this.respondedInTime;
        }

        public Socket getClientSocket() {
            return this.clientSocket;
        }
    }
}
