package referee;

import com.google.common.collect.Iterators;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import map.Destination;
import map.IRailConnection;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.YenShortestPathIterator;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 * Utility class for graph algorithms related to scoring players.
 */
public class ScoreGraphUtils {

    /**
     * Determines the set of players who have the longest path (ties allowed). If all players have a
     * longest path of length 0, then all players have that longest path.
     *
     * @param playerConnectionGraphs the graphs of owned connections from which longest paths are
     *                               calculated.
     * @return a Set of Integer where each Integer is the index of the graph in the given list of
     * graphs that has or ties for the longest path. The set will be empty iff the given list is
     * empty.
     */
    public static Set<Integer> calculatePlayersWithLongestPath(
        List<Graph<String, DefaultWeightedEdge>> playerConnectionGraphs) {
        int overallLongestPathLength = 0;
        Set<Integer> result = new HashSet<>();
        for (int index = 0; index < playerConnectionGraphs.size(); index += 1) {
            int longestPathLength = calculateLongestPathLength(playerConnectionGraphs.get(index));
            if (longestPathLength > overallLongestPathLength) {
                overallLongestPathLength = longestPathLength;
                result.clear();
                result.add(index);
            } else if (longestPathLength == overallLongestPathLength) {
                result.add(index);
            }
        }
        return result;
    }

    private static int calculateLongestPathLength(
        Graph<String, DefaultWeightedEdge> occupiedConnectionsGraph) {
        int longestPath = 0;
        // TODO: make this more efficient.
        // This solution is horribly inefficient, and was only chosen because it maximally pushed
        // the work onto other libraries
        for (String vertex1 : occupiedConnectionsGraph.vertexSet()) {
            for (String vertex2 : occupiedConnectionsGraph.vertexSet()) {
                if (!vertex1.equals(vertex2)) {
                    Iterator<GraphPath<String, DefaultWeightedEdge>> pathIterator =
                        new YenShortestPathIterator<>(occupiedConnectionsGraph, vertex1, vertex2);
                    int longestPathBetweenVertices = 0;
                    if (pathIterator.hasNext()) {
                        longestPathBetweenVertices = (int) Iterators.getLast(pathIterator).getWeight();
                    }
                    longestPath = (int) Math.max(longestPath, longestPathBetweenVertices);
                }
            }
        }
        return longestPath;
    }

    /**
     * Constructs a simple graph from the given set of connections. The vertices of the graph are
     * the names of the cities specified as endpoints on the connections and the edges are weighted
     * by the length of the longest connection between two cities.
     *
     * @param ownedConnections the set of connections providing edges and implicitly vertices for
     *                         this graph.
     * @return the graph of String vertices and DefaultWeightEdge edges.
     */
    public static Graph<String, DefaultWeightedEdge> occupiedConnectionsToGraph(
        Set<IRailConnection> ownedConnections) {
        Graph<String, DefaultWeightedEdge> occupiedConnectionsGraph =
            new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        // add all edges and nodes from occupied connections
        for (IRailConnection connection : ownedConnections) {
            String city1Name = connection.getCities().left.getName();
            String city2Name = connection.getCities().right.getName();
            occupiedConnectionsGraph.addVertex(city1Name);
            occupiedConnectionsGraph.addVertex(city2Name);

            if (!occupiedConnectionsGraph.containsEdge(city1Name, city2Name)) {
                DefaultWeightedEdge newEdge = occupiedConnectionsGraph
                    .addEdge(city1Name, city2Name);
                occupiedConnectionsGraph.setEdgeWeight(newEdge, connection.getLength());
            } else {
                // We only want the graph to contain the edge with the longest length between two
                // cities because the shorter edges necessarily are not included in any longest
                // simple paths.
                DefaultWeightedEdge oldEdge = occupiedConnectionsGraph
                    .getEdge(city1Name, city2Name);
                occupiedConnectionsGraph.setEdgeWeight(
                    oldEdge,
                    Math.max(occupiedConnectionsGraph.getEdgeWeight(oldEdge),
                        connection.getLength()));
            }
        }

        return occupiedConnectionsGraph;
    }

    /**
     * Calculates the number of the given destinations that are completed by the given graph of
     * occupied connections.
     *
     * @param occupiedConnectionsGraph the graph of occupied connections.
     * @param destinations             the destinations to complete.
     * @return an integer in the range [0, destinations.size()] representing the number of
     * destinations completed.
     */
    public static int calculateNumDestinationsConnected(
        Graph<String, DefaultWeightedEdge> occupiedConnectionsGraph,
        Set<Destination> destinations) {
        int numDestinationsConnected = 0;
        ConnectivityInspector<String, DefaultWeightedEdge> connectivityInspector =
            new ConnectivityInspector<>(occupiedConnectionsGraph);

        for (Destination selectedDestination : destinations) {
            String city1Name = selectedDestination.left.getName();
            String city2Name = selectedDestination.right.getName();

            if (occupiedConnectionsGraph.containsVertex(city1Name)
                && occupiedConnectionsGraph.containsVertex(city2Name)
                && connectivityInspector.pathExists(city1Name, city2Name)) {
                numDestinationsConnected++;
            }
        }

        return numDestinationsConnected;
    }
}
