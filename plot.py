"""Builds plots from results/tables/results.csv. Run: python3 plot.py"""
import csv
from collections import defaultdict
import matplotlib.pyplot as plt

rows = list(csv.DictReader(open("results/tables/results.csv")))
data = defaultdict(list)
for r in rows:
    data[r["algorithm"]].append((int(r["V"]), int(r["E"]), float(r["avg_time_ms"])))

for key, idx, label, name in [("V", 0, "Vertices (V)", "time_vs_V.png"),
                              ("E", 1, "Edges (E)", "time_vs_E.png")]:
    plt.figure(figsize=(7, 4.5))
    for alg, pts in data.items():
        plt.plot([p[idx] for p in pts], [p[2] for p in pts], marker="o", label=alg)
    plt.xscale("log")
    plt.yscale("log")
    plt.xlabel(label)
    plt.ylabel("Average time, ms (log scale)")
    plt.title(f"Running time vs {key}")
    plt.grid(True, which="both", alpha=0.3)
    plt.legend(fontsize=8)
    plt.tight_layout()
    plt.savefig(f"results/plots/{name}", dpi=120)
    plt.close()
print("Plots saved to results/plots")
