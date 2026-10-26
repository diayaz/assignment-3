package app;

import connectivity.SCC;
import connectivity.TopologicalSort;
import dsu.DSU;
import graph.Graph;
import graph.GraphGenerator;
import mst.Kruskal;
import mst.MSTResult;
import mst.Prim;
import shortestpath.Dijkstra;

import java.util.List;

/** Simple test runner without external libraries. Exit code 1 if any test fails. */
public class Tests {
    static int passed = 0, failed = 0;

    static void check(String name, boolean ok) {
        if (ok) passed++; else failed++;
        System.out.println((ok ? "PASS  " : "FAIL  ") + name);
    }

    public static void main(String[] args) {
        // ---------- DSU ----------
        DSU d = new DSU(5);
        d.union(0, 1);
        d.union(3, 4);
        check("DSU: union/find", d.connected(0, 1) && d.connected(3, 4) && !d.connected(1, 3));
        check("DSU: component count", d.components() == 3);
        check("DSU: union of same set returns false", !d.union(1, 0));

        // ---------- MST ----------
        Graph small = new Graph(4, false);
        small.addEdge(0, 1, 1);
        small.addEdge(1, 2, 2);
        small.addEdge(2, 3, 3);
        small.addEdge(0, 3, 4);
        small.addEdge(0, 2, 5);
        check("MST small: Kruskal weight = 6", Kruskal.run(small).totalWeight == 6);
        check("MST small: Prim weight = 6", Prim.run(small).totalWeight == 6);

        Graph disc = new Graph(5, false); // {0,1,2} and {3,4}
        disc.addEdge(0, 1, 1);
        disc.addEdge(1, 2, 2);
        disc.addEdge(3, 4, 3);
        MSTResult kd = Kruskal.run(disc), pd = Prim.run(disc);
        check("MST disconnected: detected by Kruskal and Prim", !kd.connected && !pd.connected);
        check("MST disconnected: forest weights equal", kd.totalWeight == 6 && pd.totalWeight == 6);

        Graph multi = new Graph(4, false); // square with all weights 1 -> 4 different MSTs
        multi.addEdge(0, 1, 1);
        multi.addEdge(1, 2, 1);
        multi.addEdge(2, 3, 1);
        multi.addEdge(3, 0, 1);
        check("MST multiple valid MSTs / duplicate weights: weight = 3",
                Kruskal.run(multi).totalWeight == 3 && Prim.run(multi).totalWeight == 3);

        Graph one = new Graph(1, false);
        check("MST single vertex: weight 0, connected",
                Kruskal.run(one).totalWeight == 0 && Kruskal.run(one).connected && Prim.run(one).connected);

        boolean allSame = true;
        for (long seed = 1; seed <= 50; seed++) {
            Graph g = GraphGenerator.connectedUndirected(60, 300, seed);
            MSTResult k = Kruskal.run(g), p = Prim.run(g);
            if (k.totalWeight != p.totalWeight || k.edges.size() != 59 || p.edges.size() != 59) allSame = false;
        }
        check("MST: Kruskal == Prim on 50 random connected graphs", allSame);

        // ---------- Topological sort ----------
        boolean allValid = true;
        for (long seed = 1; seed <= 30; seed++) {
            Graph g = GraphGenerator.dag(50, 200, seed);
            if (!TopologicalSort.isValidOrder(g, TopologicalSort.sort(g))) allValid = false;
        }
        check("TopoSort: valid order for 30 random DAGs", allValid);

        Graph cyc = new Graph(3, true);
        cyc.addEdge(0, 1, 1);
        cyc.addEdge(1, 2, 1);
        cyc.addEdge(2, 0, 1);
        check("TopoSort: cycle detected", TopologicalSort.sort(cyc) == null);
        check("TopoSort: single vertex", TopologicalSort.sort(new Graph(1, true)).size() == 1);

        // ---------- SCC ----------
        Graph s = new Graph(6, true);
        s.addEdge(0, 1, 1);
        s.addEdge(1, 2, 1);
        s.addEdge(2, 0, 1);
        s.addEdge(2, 3, 1);
        s.addEdge(3, 4, 1);
        s.addEdge(4, 3, 1);
        // vertex 5 isolated
        List<List<Integer>> comps = SCC.find(s);
        check("SCC: 3 components ({0,1,2}, {3,4}, {5})", comps.size() == 3);

        boolean exactlyOnce = true;
        for (long seed = 1; seed <= 20; seed++) {
            Graph g = GraphGenerator.randomDirected(100, 300, seed);
            int[] count = new int[100];
            for (List<Integer> c : SCC.find(g)) for (int v : c) count[v]++;
            for (int c : count) if (c != 1) exactlyOnce = false;
        }
        check("SCC: every vertex in exactly one component", exactlyOnce);
        check("SCC: DAG -> every vertex is its own component",
                SCC.find(GraphGenerator.dag(50, 200, 7)).size() == 50);

        // ---------- Dijkstra ----------
        Graph r = new Graph(5, true);
        r.addEdge(0, 1, 4);
        r.addEdge(0, 2, 1);
        r.addEdge(2, 1, 2);
        r.addEdge(1, 3, 1);
        // vertex 4 unreachable
        Dijkstra dj = new Dijkstra(r, 0);
        check("Dijkstra: distances", dj.dist[1] == 3 && dj.dist[2] == 1 && dj.dist[3] == 4);
        check("Dijkstra: path 0->2->1->3", dj.path(3).equals(List.of(0, 2, 1, 3)));
        check("Dijkstra: unreachable vertex", !dj.reachable(4) && dj.path(4).isEmpty());
        check("Dijkstra: single vertex", new Dijkstra(new Graph(1, true), 0).dist[0] == 0);

        boolean matchesBF = true;
        for (long seed = 1; seed <= 20; seed++) {
            Graph g = GraphGenerator.randomDirected(80, 400, seed);
            if (!java.util.Arrays.equals(new Dijkstra(g, 0).dist, bellmanFord(g, 0))) matchesBF = false;
        }
        check("Dijkstra: equals Bellman-Ford on 20 random graphs", matchesBF);

        Graph neg = new Graph(2, true);
        neg.addEdge(0, 1, -1);
        boolean thrown = false;
        try { new Dijkstra(neg, 0); } catch (IllegalArgumentException ex) { thrown = true; }
        check("Dijkstra: rejects negative weights", thrown);

        System.out.println("\n" + passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }

    /** Reference implementation used only to verify Dijkstra. */
    static long[] bellmanFord(Graph g, int src) {
        long[] dist = new long[g.vertexCount()];
        java.util.Arrays.fill(dist, Dijkstra.INF);
        dist[src] = 0;
        for (int i = 0; i < g.vertexCount() - 1; i++)
            for (graph.Edge e : g.edges())
                if (dist[e.from] != Dijkstra.INF && dist[e.from] + e.weight < dist[e.to])
                    dist[e.to] = dist[e.from] + e.weight;
        return dist;
    }
}
