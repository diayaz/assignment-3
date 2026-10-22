package connectivity;

import graph.Edge;
import graph.Graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Kosaraju's algorithm (iterative DFS, so no stack overflow on big graphs).
 * 1) DFS on G, record vertices by finish time.
 * 2) DFS on reversed G in decreasing finish time; each tree is one SCC.
 */
public class SCC {
    public static long edgeExaminations = 0;

    public static List<List<Integer>> find(Graph g) {
        edgeExaminations = 0;
        int n = g.vertexCount();
        boolean[] visited = new boolean[n];
        List<Integer> finishOrder = new ArrayList<>(n);

        for (int s = 0; s < n; s++) {
            if (visited[s]) continue;
            ArrayDeque<int[]> stack = new ArrayDeque<>(); // {vertex, next neighbor index}
            stack.push(new int[]{s, 0});
            visited[s] = true;
            while (!stack.isEmpty()) {
                int[] top = stack.peek();
                List<Edge> nb = g.neighbors(top[0]);
                if (top[1] < nb.size()) {
                    int to = nb.get(top[1]++).to;
                    edgeExaminations++;
                    if (!visited[to]) {
                        visited[to] = true;
                        stack.push(new int[]{to, 0});
                    }
                } else {
                    finishOrder.add(top[0]);
                    stack.pop();
                }
            }
        }

        Graph rev = g.reversed();
        boolean[] assigned = new boolean[n];
        List<List<Integer>> components = new ArrayList<>();
        for (int i = n - 1; i >= 0; i--) {
            int s = finishOrder.get(i);
            if (assigned[s]) continue;
            List<Integer> comp = new ArrayList<>();
            ArrayDeque<Integer> stack = new ArrayDeque<>();
            stack.push(s);
            assigned[s] = true;
            while (!stack.isEmpty()) {
                int v = stack.pop();
                comp.add(v);
                for (Edge e : rev.neighbors(v)) {
                    edgeExaminations++;
                    if (!assigned[e.to]) {
                        assigned[e.to] = true;
                        stack.push(e.to);
                    }
                }
            }
            components.add(comp);
        }
        return components;
    }
}
