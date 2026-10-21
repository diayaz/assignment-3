package mst;

import graph.Edge;
import graph.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Prim (lazy version) with a binary-heap PriorityQueue.
 * If the graph is disconnected it restarts from every unvisited vertex and returns a spanning forest.
 */
public class Prim {
    public static MSTResult run(Graph g) {
        int n = g.vertexCount();
        boolean[] inTree = new boolean[n];
        long[] cmp = {0};
        PriorityQueue<Edge> pq = new PriorityQueue<>((a, b) -> { cmp[0]++; return Integer.compare(a.weight, b.weight); });
        List<Edge> mst = new ArrayList<>();
        long total = 0, examined = 0;
        int trees = 0;

        for (int start = 0; start < n; start++) {
            if (inTree[start]) continue;
            trees++;
            examined += visit(g, start, inTree, pq);
            while (!pq.isEmpty()) {
                Edge e = pq.poll();
                if (inTree[e.to]) continue; // outdated entry
                mst.add(e);
                total += e.weight;
                examined += visit(g, e.to, inTree, pq);
            }
        }
        boolean connected = trees <= 1;
        return new MSTResult(mst, total, connected, examined, cmp[0], 0);
    }

    private static long visit(Graph g, int v, boolean[] inTree, PriorityQueue<Edge> pq) {
        inTree[v] = true;
        long examined = 0;
        for (Edge e : g.neighbors(v)) {
            examined++;
            if (!inTree[e.to]) pq.add(e);
        }
        return examined;
    }
}
