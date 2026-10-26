package app;

import connectivity.SCC;
import connectivity.TopologicalSort;
import graph.Graph;
import mst.Kruskal;
import mst.MSTResult;
import mst.Prim;
import shortestpath.Dijkstra;

import java.util.List;

/** Small demo on a hand-made city with 6 districts. */
public class Main {
    static final String[] NAMES = {"Center", "North", "South", "East", "West", "Airport"};

    public static void main(String[] args) {
        // ---- Task 1: MST (road construction costs) ----
        Graph city = new Graph(6, false);
        city.addEdge(0, 1, 4);
        city.addEdge(0, 2, 3);
        city.addEdge(1, 2, 1);
        city.addEdge(1, 3, 2);
        city.addEdge(2, 3, 4);
        city.addEdge(3, 4, 2);
        city.addEdge(4, 5, 6);
        city.addEdge(3, 5, 7);

        MSTResult k = Kruskal.run(city);
        MSTResult p = Prim.run(city);
        System.out.println("=== MST ===");
        System.out.println("Kruskal: weight=" + k.totalWeight + " edges=" + k.edges + " connected=" + k.connected);
        System.out.println("Prim:    weight=" + p.totalWeight + " edges=" + p.edges + " connected=" + p.connected);
        System.out.println("Same weight: " + (k.totalWeight == p.totalWeight));

        // ---- Task 2: topological order and SCC (one-way roads / service dependencies) ----
        Graph deps = new Graph(6, true); // e.g. power -> water -> ...
        deps.addEdge(0, 1, 1);
        deps.addEdge(0, 2, 1);
        deps.addEdge(1, 3, 1);
        deps.addEdge(2, 3, 1);
        deps.addEdge(3, 4, 1);
        deps.addEdge(4, 5, 1);
        System.out.println("\n=== Topological order (DAG) ===");
        System.out.println(TopologicalSort.sort(deps));

        Graph oneWay = new Graph(6, true);
        oneWay.addEdge(0, 1, 1);
        oneWay.addEdge(1, 2, 1);
        oneWay.addEdge(2, 0, 1); // cycle 0-1-2
        oneWay.addEdge(2, 3, 1);
        oneWay.addEdge(3, 4, 1);
        oneWay.addEdge(4, 3, 1); // cycle 3-4
        oneWay.addEdge(4, 5, 1);
        List<Integer> order = TopologicalSort.sort(oneWay);
        System.out.println("\n=== One-way roads ===");
        System.out.println("Topological order: " + (order == null ? "impossible, graph has a cycle" : order));
        System.out.println("SCCs: " + SCC.find(oneWay));

        // ---- Task 3: Dijkstra (travel distances) ----
        Graph roads = new Graph(6, true);
        roads.addEdge(0, 1, 7);
        roads.addEdge(0, 2, 2);
        roads.addEdge(2, 1, 3);
        roads.addEdge(1, 3, 1);
        roads.addEdge(2, 3, 8);
        roads.addEdge(3, 4, 2);
        // vertex 5 (Airport) has no incoming roads -> unreachable
        Dijkstra d = new Dijkstra(roads, 0);
        System.out.println("\n=== Dijkstra from " + NAMES[0] + " ===");
        for (int v = 0; v < 6; v++) {
            if (d.reachable(v)) System.out.println(NAMES[v] + ": dist=" + d.dist[v] + " path=" + d.path(v));
            else System.out.println(NAMES[v] + ": unreachable");
        }
    }
}
