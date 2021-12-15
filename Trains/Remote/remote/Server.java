package remote;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;
import game_state.RailCard;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import map.Destination;
import map.ITrainMap;
import player.IPlayer;
import test_utils.TrainsMapUtils;
import tournament_manager.ITournamentManager;
import tournament_manager.SingleElimTournamentManager;
import tournament_manager.TournamentResult;
import utils.SynchronizedCounter;

/**
 * A server that will run a tournament of games of Trains.
 */
public class Server {

    private static final int NAME_TIMEOUT_MILLIS = 3000;
    private static final int ONE_CONNECTION_WAITING_TIMEOUT_MILLIS = 1000;
    private static final int PLAYERS_CONNECTION_WAITING_TIMEOUT_MILLIS = 20000;
    private static final int MIN_CONNECTIONS_FOR_TOURNAMENT_ROUND_1 = 5;
    private static final int MIN_CONNECTIONS_FOR_TOURNAMENT_ROUND_2 = 2;
    private static final int MAX_CONNECTIONS_FOR_TOURNAMENT = 50;

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

    /**
     * Runs the tournament.
     *
     * Sets up the tournament according to the following steps:
     * 1. accept sign ups for 20 seconds
     * 2. if fewer than MIN_CONNECTIONS_FOR_TOURNAMENT_ROUND_1 have signed up, wait another 20 seconds
     * 3. if fewer than MIN_CONNECTIONS_FOR_TOURNAMENT_ROUND_2 have signed up, cancel the tournament
     * 4. run the tournament with all signed up players
     *
     * If at any point during the two waiting periods MAX_CONNECTIONS_FOR_TOURNAMENT have signed up,
     * the tournament will start immediately.
     *
     * @return The result of the tournament, or an empty result if it was cancelled
     */
    public TournamentResult run() throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(this.port);
        serverSocket.setSoTimeout(ONE_CONNECTION_WAITING_TIMEOUT_MILLIS);
        List<ClientHandler> signedUpPlayerHandlers = new ArrayList<>();
        List<Thread> signedUpPlayerThreads = new ArrayList<>();

        // Counts the number of successful sign ups across all sign up threads
        SynchronizedCounter counter = new SynchronizedCounter();

        waitForPlayers(serverSocket, signedUpPlayerHandlers, signedUpPlayerThreads, counter);

        if (signedUpPlayerHandlers.size() < MIN_CONNECTIONS_FOR_TOURNAMENT_ROUND_1) {
            waitForPlayers(serverSocket, signedUpPlayerHandlers, signedUpPlayerThreads, counter);
        }

        LinkedHashMap<String, IPlayer> players = getSignedUpPlayers(signedUpPlayerHandlers,
            signedUpPlayerThreads);

        if (players.size() < MIN_CONNECTIONS_FOR_TOURNAMENT_ROUND_2) {
            return new TournamentResult(new HashSet<>(), new HashSet<>());
        }

        ITournamentManager manager = new SingleElimTournamentManager.SingleElimTournamentManagerBuilder()
            .deckProvider(this.deckSupplier)
            .destinationProvider(this.destinationProvider)
            .mapSelector(this.mapSelector)
            .build();

        return manager.runTournament(players);
    }

    /**
     * Parses all of the threads responsible for signing up players for the names of the players
     * who successfully signed up.
     *
     * @param signedUpPlayerHandlers The handlers for all players that connected to the server.
     * @param signedUpPlayerThreads The threads that ran the handlers for all players that connected
     *                             to the server
     * @return A map of all players that signed up successfully.
     */
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
                String name = handler.getName();
                boolean insertedPlayer = false;
                while (!insertedPlayer) {
                    if (!players.containsKey(name)) {
                        players.put(name, new ProxyPlayer(handler.getClientSocket()));
                        insertedPlayer = true;
                    }
                    else {
                        name = name.concat("a");
                    }
                }
            }
        }
        return players;
    }

    /**
     * Waits PLAYERS_CONNECTION_WAITING_TIMEOUT_MILLIS for players to connect to the server. All
     * incoming connections are dispatched to ClientHandlers to finish signing up. If at any point
     * MAX_CONNECTIONS_FOR_TOURNAMENT players have signed up, return immediately.
     *
     * @param serverSocket The socket to accept connections on.
     * @param signedUpPlayerHandlers All handlers that are signing up players.
     * @param signedUpPlayerThreads All threads running handlers that are singing up players
     * @param counter The thread safe count of all players that have finished signing up.
     */
    private static void waitForPlayers(ServerSocket serverSocket,
        List<ClientHandler> signedUpPlayerHandlers,
        List<Thread> signedUpPlayerThreads,
        SynchronizedCounter counter)
        throws IOException {

        long startTime = System.currentTimeMillis();
        while (counter.get() < MAX_CONNECTIONS_FOR_TOURNAMENT
            && System.currentTimeMillis() - startTime < PLAYERS_CONNECTION_WAITING_TIMEOUT_MILLIS) {

            try {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, counter);
                Thread clientHandlerThread = new Thread(handler);
                clientHandlerThread.start();
                signedUpPlayerHandlers.add(handler);
                signedUpPlayerThreads.add(clientHandlerThread);
            } catch (SocketTimeoutException ignored) {
                // correct behavior after timeout on accept is to go to the next iteration of the loop
            }
        }
    }

    /**
     * Handles completing the sign-up of a player after they have connected to the server.
     */
    public static class ClientHandler implements Runnable {

        private final Socket clientSocket;
        private final InputStream input;
        private volatile String name;
        private volatile boolean respondedInTime = false;
        private SynchronizedCounter counter;

        public ClientHandler(Socket clientSocket, SynchronizedCounter counter) throws IOException {
            this.clientSocket = clientSocket;
            this.input = clientSocket.getInputStream();
            this.counter = counter;
        }

        public ClientHandler(InputStream in, SynchronizedCounter counter) {
            this.clientSocket = null;
            this.input = in;
            this.counter = counter;
        }

        @Override
        public void run() {
            InputStreamReader inputStreamReader = new InputStreamReader(this.input);
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < NAME_TIMEOUT_MILLIS) {
                try {
                    if (inputStreamReader.ready()) {
                        char[] buff = new char[51];
                        inputStreamReader.read(buff);
                        JsonStreamParser parser = new JsonStreamParser(new CharArrayReader(buff));
                        JsonElement jsonName = parser.next();
                        if (validateName(jsonName)) {
                            this.name = jsonName.getAsString();
                            this.respondedInTime = true;
                            this.counter.increment();
                            return;
                        }
                    }
                } catch (Exception e) {
                    return;
                }
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

        private static boolean validateName(JsonElement jsonName) {
            if (Objects.isNull(jsonName) || !jsonName.isJsonPrimitive()) {
                return false;
            }
            String name = jsonName.getAsString();
            boolean correct_length = name.length() > 0 && name.length() <= 50;
            boolean correct_characters = Pattern.matches("[a-zA-Z]+", name);
            return correct_length && correct_characters;
        }
    }
}
