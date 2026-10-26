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

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Runs every algorithm on Small / Medium / Large graphs, 5 times each,
 * and writes the average time (System.nanoTime) and operation counts to results/tables/.
 */
public class Benchmark {
    static final long SEED = 42;
    static final int RUNS = 5;
    static final int WARMUP = 20;
    static final String[] NAMES = {"Small", "Medium", "Large"};
    static final int[] V = {10, 100, 1000};
    static final int[] E = {30, 500, 5000};

    interface Task { long run(); } // returns main operation count

    static double avgMs(Task t, long[] opsOut) {
        for (int i = 0; i < WARMUP; i++) t.run(); // JIT warm-up, not measured
        long total = 0;
        for (int i = 0; i < RUNS; i++) {
            long s = System.nanoTime();
            opsOut[0] = t.run();
            total += System.nanoTime() - s;
        }
        return total / (double) RUNS / 1_000_000.0;
    }

    public static void main(String[] args) throws IOException {
        String dir = args.length > 0 ? args[0] : "results/tables";
        try (PrintWriter csv = new PrintWriter(new FileWriter(dir + "/results.csv"));
             PrintWriter md = new PrintWriter(new FileWriter(dir + "/results.md"))) {

            csv.println("algorithm,graph,V,E,avg_time_ms,operations,details");
            md.println("| Algorithm | Graph | V | E | Avg time (ms) | Operations | Details |");
            md.println("|---|---|---|---|---|---|---|");

            for (int i = 0; i < 3; i++) {
                int v = V[i], e = E[i];
                Graph und = GraphGenerator.connectedUndirected(v, e, SEED);
                Graph dag = GraphGenerator.dag(v, e, SEED);
                Graph cyc = GraphGenerator.randomDirected(v, e, SEED);
                long[] ops = new long[1];

                // Kruskal
                MSTResult kr = Kruskal.run(und);
                double t = avgMs(() -> Kruskal.run(und).edgeExaminations, ops);
                row(csv, md, "Kruskal", i, v, e, t, kr.edgeExaminations + kr.comparisons + kr.dsuOperations,
                        "weight=" + kr.totalWeight + "; edgeExam=" + kr.edgeExaminations
                                + "; comparisons=" + kr.comparisons + "; dsuOps=" + kr.dsuOperations);

                // Prim
                MSTResult pr = Prim.run(und);
                t = avgMs(() -> Prim.run(und).edgeExaminations, ops);
                row(csv, md, "Prim", i, v, e, t, pr.edgeExaminations + pr.comparisons,
                        "weight=" + pr.totalWeight + "; edgeExam=" + pr.edgeExaminations
                                + "; comparisons=" + pr.comparisons);

                // Topological sort on DAG and on cyclic graph
                List<Integer> ord = TopologicalSort.sort(dag);
                long topoOps = TopologicalSort.edgeExaminations;
                t = avgMs(() -> { TopologicalSort.sort(dag); return TopologicalSort.edgeExaminations; }, ops);
                row(csv, md, "TopoSort (DAG)", i, v, e, t, topoOps,
                        "valid=" + TopologicalSort.isValidOrder(dag, ord));

                List<Integer> ordC = TopologicalSort.sort(cyc);
                long topoCOps = TopologicalSort.edgeExaminations;
                t = avgMs(() -> { TopologicalSort.sort(cyc); return TopologicalSort.edgeExaminations; }, ops);
                row(csv, md, "TopoSort (cyclic)", i, v, e, t, topoCOps,
                        "cycleDetected=" + (ordC == null));

                // SCC on cyclic graph
                List<List<Integer>> sccs = SCC.find(cyc);
                long sccOps = SCC.edgeExaminations;
                t = avgMs(() -> { SCC.find(cyc); return SCC.edgeExaminations; }, ops);
                int biggest = sccs.stream().mapToInt(List::size).max().orElse(0);
                row(csv, md, "SCC (Kosaraju)", i, v, e, t, sccOps,
                        "components=" + sccs.size() + "; largest=" + biggest);

                // Dijkstra from vertex 0 on directed weighted graph
                Dijkstra dj = new Dijkstra(cyc, 0);
                int reach = 0;
                for (int x = 0; x < v; x++) if (dj.reachable(x)) reach++;
                t = avgMs(() -> new Dijkstra(cyc, 0).edgeRelaxations, ops);
                row(csv, md, "Dijkstra", i, v, e, t, dj.edgeRelaxations + dj.pqOperations,
                        "relaxations=" + dj.edgeRelaxations + "; pqOps=" + dj.pqOperations + "; reachable=" + reach);
            }
        }

        // DSU: optimized vs naive on the worst case (a long chain)
        try (PrintWriter dsu = new PrintWriter(new FileWriter(dir + "/dsu.md"))) {
            dsu.println("| n | Naive: parent steps | Optimized: parent steps | Naive time (ms) | Optimized time (ms) |");
            dsu.println("|---|---|---|---|---|");
            for (int n : V) {
                long[] ops = new long[1];
                long naiveSteps = dsuChain(n, false);
                double tn = avgMs(() -> dsuChain(n, false), ops);
                long optSteps = dsuChain(n, true);
                double to = avgMs(() -> dsuChain(n, true), ops);
                dsu.printf(java.util.Locale.US, "| %d | %d | %d | %.4f | %.4f |%n", n, naiveSteps, optSteps, tn, to);
            }
        }
        System.out.println("Results written to " + dir);
    }

    /** union(i+1, i) for all i builds a chain without rank; then find() is called for every vertex. */
    static long dsuChain(int n, boolean optimized) {
        DSU d = new DSU(n, optimized);
        for (int i = 0; i + 1 < n; i++) d.union(i + 1, i);
        for (int i = 0; i < n; i++) d.find(i);
        return d.parentSteps;
    }

    static void row(PrintWriter csv, PrintWriter md, String alg, int i, int v, int e, double t, long ops, String det) {
        csv.printf(java.util.Locale.US, "%s,%s,%d,%d,%.4f,%d,%s%n", alg, NAMES[i], v, e, t, ops, det);
        md.printf(java.util.Locale.US, "| %s | %s | %d | %d | %.4f | %d | %s |%n", alg, NAMES[i], v, e, t, ops, det);
        System.out.printf(java.util.Locale.US, "%-18s %-6s %.4f ms  ops=%d  %s%n", alg, NAMES[i], t, ops, det);
    }
}
