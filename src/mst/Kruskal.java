package mst;

import dsu.DSU;
import graph.Edge;
import graph.Graph;

import java.util.ArrayList;
import java.util.List;

/** Kruskal: sort edges by weight, add an edge if it joins two different components (checked with DSU). */
public class Kruskal {
    public static MSTResult run(Graph g) {
        int n = g.vertexCount();
        List<Edge> sorted = new ArrayList<>(g.edges());
        long[] cmp = {0};
        sorted.sort((a, b) -> { cmp[0]++; return Integer.compare(a.weight, b.weight); });

        DSU dsu = new DSU(n);
        List<Edge> mst = new ArrayList<>();
        long total = 0, examined = 0;
        for (Edge e : sorted) {
            if (mst.size() == n - 1) break;
            examined++;
            if (dsu.union(e.from, e.to)) {
                mst.add(e);
                total += e.weight;
            }
        }
        boolean connected = n == 0 || mst.size() == n - 1;
        return new MSTResult(mst, total, connected, examined, cmp[0], dsu.findCalls + dsu.unionCalls);
    }
}
