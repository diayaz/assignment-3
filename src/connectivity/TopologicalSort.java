package connectivity;

import graph.Edge;
import graph.Graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/** Kahn's algorithm. Returns null when the graph has a cycle (ordering impossible). */
public class TopologicalSort {
    public static long edgeExaminations = 0;

    public static List<Integer> sort(Graph g) {
        edgeExaminations = 0;
        int n = g.vertexCount();
        int[] indeg = new int[n];
        for (Edge e : g.edges()) indeg[e.to]++;

        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int v = 0; v < n; v++) if (indeg[v] == 0) queue.add(v);

        List<Integer> order = new ArrayList<>(n);
        while (!queue.isEmpty()) {
            int v = queue.poll();
            order.add(v);
            for (Edge e : g.neighbors(v)) {
                edgeExaminations++;
                if (--indeg[e.to] == 0) queue.add(e.to);
            }
        }
        return order.size() == n ? order : null; // some vertices never reached in-degree 0 -> cycle
    }

    /** Checks that every edge u->v has u before v in the order. */
    public static boolean isValidOrder(Graph g, List<Integer> order) {
        if (order == null || order.size() != g.vertexCount()) return false;
        int[] pos = new int[g.vertexCount()];
        for (int i = 0; i < order.size(); i++) pos[order.get(i)] = i;
        for (Edge e : g.edges()) if (pos[e.from] >= pos[e.to]) return false;
        return true;
    }
}
