package graph;

import java.util.ArrayList;
import java.util.List;

/**
 * City graph stored as an adjacency list.
 * Vertices are districts 0..n-1, edges are roads.
 */
public class Graph {
    private final int n;
    private final boolean directed;
    private final List<List<Edge>> adj;
    private final List<Edge> edges; // each edge stored once

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        this.adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        this.edges = new ArrayList<>();
    }

    public void addEdge(int u, int v, int w) {
        Edge e = new Edge(u, v, w);
        edges.add(e);
        adj.get(u).add(e);
        if (!directed) adj.get(v).add(new Edge(v, u, w));
    }

    public int vertexCount() { return n; }
    public int edgeCount() { return edges.size(); }
    public boolean isDirected() { return directed; }
    public List<Edge> neighbors(int v) { return adj.get(v); }
    public List<Edge> edges() { return edges; }

    /** Graph with all edges reversed (used by Kosaraju). */
    public Graph reversed() {
        Graph r = new Graph(n, directed);
        for (Edge e : edges) r.addEdge(e.to, e.from, e.weight);
        return r;
    }
}
