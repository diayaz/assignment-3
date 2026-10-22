package shortestpath;

import graph.Edge;
import graph.Graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/** Dijkstra with a binary-heap PriorityQueue (lazy deletion). Requires non-negative weights. */
public class Dijkstra {
    public static final long INF = Long.MAX_VALUE;

    public final long[] dist;
    public final int[] prev;
    public long edgeRelaxations = 0;
    public long pqOperations = 0;

    public Dijkstra(Graph g, int source) {
        for (Edge e : g.edges())
            if (e.weight < 0) throw new IllegalArgumentException("Dijkstra needs non-negative weights");

        int n = g.vertexCount();
        dist = new long[n];
        prev = new int[n];
        java.util.Arrays.fill(dist, INF);
        java.util.Arrays.fill(prev, -1);
        dist[source] = 0;

        PriorityQueue<long[]> pq = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0])); // {dist, vertex}
        pq.add(new long[]{0, source});
        pqOperations++;
        while (!pq.isEmpty()) {
            long[] cur = pq.poll();
            pqOperations++;
            int v = (int) cur[1];
            if (cur[0] > dist[v]) continue; // outdated entry
            for (Edge e : g.neighbors(v)) {
                edgeRelaxations++;
                long nd = dist[v] + e.weight;
                if (nd < dist[e.to]) {
                    dist[e.to] = nd;
                    prev[e.to] = v;
                    pq.add(new long[]{nd, e.to});
                    pqOperations++;
                }
            }
        }
    }

    public boolean reachable(int v) { return dist[v] != INF; }

    /** Path from source to v, or an empty list if v is unreachable. */
    public List<Integer> path(int v) {
        List<Integer> p = new ArrayList<>();
        if (!reachable(v)) return p;
        for (int x = v; x != -1; x = prev[x]) p.add(x);
        Collections.reverse(p);
        return p;
    }
}
