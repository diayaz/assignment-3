package graph;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/** Reproducible random graphs (fixed seed). Weights are in [1, 100]. No self-loops, no duplicate edges. */
public class GraphGenerator {
    public static final int MAX_WEIGHT = 100;

    /**
     * Connected undirected graph: first a random spanning tree (vertex i is joined to a random
     * earlier vertex), then random extra edges until E edges exist.
     */
    public static Graph connectedUndirected(int v, int e, long seed) {
        Random rnd = new Random(seed);
        Graph g = new Graph(v, false);
        Set<Long> used = new HashSet<>();
        for (int i = 1; i < v; i++) {
            int p = rnd.nextInt(i);
            add(g, used, p, i, rnd, false);
        }
        while (g.edgeCount() < e) {
            int a = rnd.nextInt(v), b = rnd.nextInt(v);
            if (a != b) add(g, used, a, b, rnd, false);
        }
        return g;
    }

    /** DAG: every edge goes from a smaller index to a larger one, so no cycle is possible. */
    public static Graph dag(int v, int e, long seed) {
        Random rnd = new Random(seed);
        Graph g = new Graph(v, true);
        Set<Long> used = new HashSet<>();
        while (g.edgeCount() < e) {
            int a = rnd.nextInt(v), b = rnd.nextInt(v);
            if (a == b) continue;
            add(g, used, Math.min(a, b), Math.max(a, b), rnd, true);
        }
        return g;
    }

    /** Directed graph with uniformly random edge directions; with E > V it almost surely has cycles. */
    public static Graph randomDirected(int v, int e, long seed) {
        Random rnd = new Random(seed);
        Graph g = new Graph(v, true);
        Set<Long> used = new HashSet<>();
        while (g.edgeCount() < e) {
            int a = rnd.nextInt(v), b = rnd.nextInt(v);
            if (a != b) add(g, used, a, b, rnd, true);
        }
        return g;
    }

    private static void add(Graph g, Set<Long> used, int a, int b, Random rnd, boolean directed) {
        int x = a, y = b;
        if (!directed && x > y) { int t = x; x = y; y = t; }
        long key = (long) x * 1_000_003L + y;
        if (used.add(key)) g.addEdge(a, b, 1 + rnd.nextInt(MAX_WEIGHT));
    }
}
