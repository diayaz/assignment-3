package dsu;

/**
 * Disjoint Set Union with path compression and union by rank.
 * The optimizations can be switched off to measure their effect.
 */
public class DSU {
    private final int[] parent;
    private final int[] rank;
    private final boolean optimized;
    private int components;

    // operation counters
    public long findCalls = 0;
    public long unionCalls = 0;
    public long parentSteps = 0; // how many parent links were followed

    public DSU(int n) { this(n, true); }

    public DSU(int n, boolean optimized) {
        this.parent = new int[n];
        this.rank = new int[n];
        this.optimized = optimized;
        this.components = n;
        for (int i = 0; i < n; i++) parent[i] = i;
    }

    public int find(int x) {
        findCalls++;
        int root = x;
        while (parent[root] != root) {
            root = parent[root];
            parentSteps++;
        }
        if (optimized) { // path compression: point every node on the path to the root
            while (parent[x] != root) {
                int next = parent[x];
                parent[x] = root;
                x = next;
            }
        }
        return root;
    }

    /** @return true if a and b were in different sets and are now merged. */
    public boolean union(int a, int b) {
        unionCalls++;
        int ra = find(a), rb = find(b);
        if (ra == rb) return false;
        if (optimized) { // union by rank: attach the shorter tree under the taller one
            if (rank[ra] < rank[rb]) { int t = ra; ra = rb; rb = t; }
            parent[rb] = ra;
            if (rank[ra] == rank[rb]) rank[ra]++;
        } else {
            parent[rb] = ra;
        }
        components--;
        return true;
    }

    public boolean connected(int a, int b) { return find(a) == find(b); }
    public int components() { return components; }
}
