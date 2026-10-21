package mst;

import graph.Edge;
import java.util.List;

/** Result of an MST algorithm plus operation counters. */
public class MSTResult {
    public final List<Edge> edges;
    public final long totalWeight;
    public final boolean connected; // false -> only a minimum spanning forest was found
    public final long edgeExaminations;
    public final long comparisons;
    public final long dsuOperations; // find + union (Kruskal only)

    public MSTResult(List<Edge> edges, long totalWeight, boolean connected,
                     long edgeExaminations, long comparisons, long dsuOperations) {
        this.edges = edges;
        this.totalWeight = totalWeight;
        this.connected = connected;
        this.edgeExaminations = edgeExaminations;
        this.comparisons = comparisons;
        this.dsuOperations = dsuOperations;
    }
}
