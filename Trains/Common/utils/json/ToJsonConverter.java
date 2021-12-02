package utils.json;

import static utils.ComparatorUtils.fromUnordered;

import action.TurnAction;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import game_state.IOpponentInfo;
import game_state.IPlayerGameState;
import game_state.RailCard;
import java.util.Iterator;
import map.Destination;
import map.ICity;
import map.IRailConnection;
import map.ITrainMap;
import referee.GameEndReport;
import tournament_manager.TournamentResult;
import utils.ComparatorUtils;
import utils.OrderedPair;
import utils.UnorderedPair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Util class to convert our data representations to their equivalent JSON representations.
 */
public class ToJsonConverter {

    /**
     * Converts an ITrainsMap to JSON.
     *
     * @param map the ITrainsMap
     * @return the JSON representation of the ITrainsMaps
     */
    public static JsonObject mapToJson(ITrainMap map) {
        int width = map.getMapDimension().getWidth();
        int height = map.getMapDimension().getHeight();

        Set<ICity> cities = map.getCities();
        JsonArray jsonCities = new JsonArray();

        for (ICity city : cities) {
            jsonCities.add(cityToJson(city, width, height));
        }

        JsonObject jsonConnections = connectionsToJson(map.getRailConnections());

        JsonObject jsonMap = new JsonObject();
        jsonMap.add("width", new JsonPrimitive(width));
        jsonMap.add("height", new JsonPrimitive(height));
        jsonMap.add("cities", jsonCities);
        jsonMap.add("connections", jsonConnections);

        return jsonMap;
    }

    /**
     * Converts a set of IRailConnections to JSON
     *
     * @param connections the IRailConnections
     * @return the JSON representation of the IRailConnections
     */
    public static JsonObject connectionsToJson(Set<IRailConnection> connections) {

        JsonObject jsonConnections = new JsonObject();

        for (IRailConnection connection : connections) {
            UnorderedPair<ICity> connectionCities = connection.getCities();
            OrderedPair<ICity> citiesOrdered = ComparatorUtils.fromUnordered(connectionCities);
            String sourceName = citiesOrdered.first.getName();
            String targetName = citiesOrdered.second.getName();

            JsonObject segment = new JsonObject();
            segment
                .add(connection.getColor().toString().toLowerCase(), new JsonPrimitive(connection.getLength()));

            if (jsonConnections.keySet().contains(sourceName)) {
                // the source and target already exist
                if (jsonConnections.getAsJsonObject(sourceName).keySet().contains(targetName)) {
                    jsonConnections.getAsJsonObject(sourceName).getAsJsonObject(targetName)
                        .add(connection.getColor().toString().toLowerCase(),
                            new JsonPrimitive(connection.getLength()));
                }
                // the source already exists, but the target doesn't yet
                else {
                    jsonConnections.getAsJsonObject(sourceName).add(targetName, segment);
                }
            }
            // neither the source nor target exist yet
            else {
                JsonObject target = new JsonObject();
                target.add(targetName, segment);

                jsonConnections.add(sourceName, target);
            }
        }

        return jsonConnections;
    }

    /**
     * Converts an ICity to JSON
     *
     * @param city      the ICity
     * @param mapWidth  the width of the ITrainsMap that the city is a part of
     * @param mapHeight the height of the ITrainsMap that the city is a part of
     * @return the JSON representation of the ICity
     */
    public static JsonArray cityToJson(ICity city, int mapWidth, int mapHeight) {
        String name = city.getName();
        Double xCoord = city.getRelativePosition().first;
        Double yCoord = city.getRelativePosition().second;

        int xCoordAbsolute = (int) (xCoord * mapWidth);
        int yCoordAbsolute = (int) (yCoord * mapHeight);

        JsonArray coords = new JsonArray();
        coords.add(xCoordAbsolute);
        coords.add(yCoordAbsolute);

        JsonArray jsonCity = new JsonArray();
        jsonCity.add(name);
        jsonCity.add(coords);

        return jsonCity;
    }

    /**
     * Converts a list of RailCards to their JSON representation
     *
     * @param cards the List of RailCard
     * @return the JSON representation of the list of RailCard ([Card, ..., Card])
     */
    public static JsonArray railCardsToJsonArray(List<RailCard> cards) {
        JsonArray jsonCards = new JsonArray();
        for (RailCard card : cards) {
            jsonCards.add(card.name().toLowerCase());
        }
        return jsonCards;
    }

    public static JsonObject railCardsToJsonObject(Map<RailCard, Integer> cards) {
        JsonObject cardsMapJson = new JsonObject();
        for (Map.Entry<RailCard, Integer> cardEntry : cards.entrySet()) {
            cardsMapJson.add(cardEntry.getKey().toString().toLowerCase(),
                    new JsonPrimitive(cardEntry.getValue()));
        }
        return cardsMapJson;
    }

    /**
     * Converts a set of Destinations to their JSON representation.
     *
     * @param destinations The Set of Destinations to convert.
     * @return The JSON representation of the destinations.
     */
    public static JsonArray destinationsToJson(Set<Destination> destinations) {
        JsonArray jsonDestinations = new JsonArray();
        for (Destination destination : destinations) {
            jsonDestinations.add(destinationToJson(destination));
        }
        return jsonDestinations;
    }

    /**
     * Converts one destination into its JSON representation.
     *
     * @param destination The Destination to convert.
     * @return The JSON representation of the Destination.
     */
    public static JsonArray destinationToJson(Destination destination) {
        JsonArray jsonDestination = new JsonArray();
        jsonDestination.add(destination.left.getName());
        jsonDestination.add(destination.right.getName());
        return jsonDestination;
    }

    /**
     * Converts a TurnAction into its JSON representation.
     *
     * @param turnAction The TurnAction to convert.
     * @return The JSON representation of the TurnAction.
     */
    public static JsonElement turnActionToJSON(TurnAction turnAction) {
        return new ActionToJSONVisitor().apply(turnAction);
    }

    /**
     * Converts an IRailConnection into its JSON representation.
     *
     * @param railConnection The IRailConnection to convert.
     * @return The JSON representation of the IRailConnection.
     */
    public static JsonArray railConnectionToJSON(IRailConnection railConnection) {
        JsonArray acquired = new JsonArray();

        OrderedPair<ICity> orderedCities = fromUnordered(railConnection.getCities());
        acquired.add(orderedCities.first.getName());
        acquired.add(orderedCities.second.getName());

        acquired.add(railConnection.getColor().name().toLowerCase());
        acquired.add(railConnection.getLength());

        return acquired;
    }

    /**
     * Converts a Set of IRailConnections into their JSON representation.
     *
     * @param acquiredConnections The Set of IRailConnections to convert.
     * @return The JSON representation of the Set of IRailConnections.
     */
    public static JsonArray acquiredConnectionsToJson(Set<IRailConnection> acquiredConnections) {
        JsonArray acquireds = new JsonArray();
        for (IRailConnection connection : acquiredConnections) {
            acquireds.add(railConnectionToJSON(connection));
        }
        return acquireds;
    }

    public static JsonObject playerGameStateToJson(IPlayerGameState gameState) {
        JsonObject jsonPlayerState = new JsonObject();
        Set<Destination> chosenDestinations = gameState.getDestinations();
        Iterator<Destination> iterator = chosenDestinations.iterator();
        jsonPlayerState.add("destination1", destinationToJson(iterator.next()));
        jsonPlayerState.add("destination2", destinationToJson(iterator.next()));
        jsonPlayerState.add("rails", new JsonPrimitive(gameState.getNumRails()));
        jsonPlayerState.add("cards", railCardsToJsonObject(gameState.getCardsInHand()));
        jsonPlayerState.add("acquired", acquiredConnectionsToJson(gameState.getOwnedConnections()));

        JsonObject jsonPlayerGameState = new JsonObject();
        jsonPlayerGameState.add("this", jsonPlayerState);
        jsonPlayerGameState
            .add("acquired", opponentInfoToJson(gameState.getOpponentInfo()));
        return jsonPlayerGameState;
    }

    private static JsonArray opponentInfoToJson(List<IOpponentInfo> opponentInfo) {
        JsonArray opponentInfoJson = new JsonArray();

        for (IOpponentInfo oneOpponentInfo : opponentInfo) {
            opponentInfoJson.add(acquiredConnectionsToJson(oneOpponentInfo.getOwnedConnections()));
        }

        return opponentInfoJson;
    }

    public static JsonArray tournamentResultToJson(TournamentResult tournamentResult) {
        JsonArray reportJson = new JsonArray();
        List<String> winners = new ArrayList<>(tournamentResult.getWinners());
        List<String> cheaters = new ArrayList<>(tournamentResult.getCheaters());
        reportJson.add(rankToJson(winners));
        reportJson.add(rankToJson(cheaters));
        return reportJson;
    }

    public static JsonArray gameReportToJson(GameEndReport report) {
        JsonArray reportJson = new JsonArray();
        List<List<String>> ranking = gameReportToRanking(report);
        JsonArray rankingJson = new JsonArray();
        for (List<String> rank : ranking) {
            rankingJson.add(rankToJson(rank));
        }

        List<String> eliminatedPlayerNames = new ArrayList<>(report.getRemovedPlayerNames());
        JsonArray eliminatedPlayersJson = rankToJson(eliminatedPlayerNames);

        reportJson.add(rankingJson);
        reportJson.add(eliminatedPlayersJson);
        return reportJson;
    }

    public static List<List<String>> gameReportToRanking(GameEndReport report) {
        List<List<String>> ranking = new ArrayList<>();
        if (report.getPlayerRanking().size() < 1) {
            ranking.add(new ArrayList<>());
            return ranking;
        }
        List<String> playerNames = new ArrayList<>();
        playerNames.add(report.getPlayerRanking().get(0).getPlayerName());
        ranking.add(playerNames);
        for (int i = 1; i < report.getPlayerRanking().size(); i++) {
            if (report.getPlayerRanking().get(i).getScore() == report.getPlayerRanking().get(i - 1).getScore()) {
                ranking.get(ranking.size() - 1).add(report.getPlayerRanking().get(i).getPlayerName());
            } else {
                List<String> rank = new ArrayList<>();
                rank.add(report.getPlayerRanking().get(i).getPlayerName());
                ranking.add(rank);
            }
        }
        return ranking;
    }

    public static JsonArray rankToJson(List<String> rank) {
        List<String> sortedRank = new ArrayList<>(rank);
        Collections.sort(sortedRank);
        JsonArray rankJson = new JsonArray();
        for (String name : sortedRank) {
            rankJson.add(new JsonPrimitive(name));
        }
        return rankJson;
    }
}
