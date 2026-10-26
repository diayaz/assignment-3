| Algorithm | Graph | V | E | Avg time (ms) | Operations | Details |
|---|---|---|---|---|---|---|
| Kruskal | Small | 10 | 30 | 0.0094 | 163 | weight=129; edgeExam=14; comparisons=107; dsuOps=42 |
| Prim | Small | 10 | 30 | 0.0127 | 263 | weight=129; edgeExam=60; comparisons=203 |
| TopoSort (DAG) | Small | 10 | 30 | 0.0063 | 30 | valid=true |
| TopoSort (cyclic) | Small | 10 | 30 | 0.0012 | 0 | cycleDetected=true |
| SCC (Kosaraju) | Small | 10 | 30 | 0.0131 | 60 | components=1; largest=10 |
| Dijkstra | Small | 10 | 30 | 0.0052 | 54 | relaxations=30; pqOps=24; reachable=10 |
| Kruskal | Medium | 100 | 500 | 0.0675 | 4483 | weight=1170; edgeExam=164; comparisons=3827; dsuOps=492 |
| Prim | Medium | 100 | 500 | 0.0685 | 8697 | weight=1170; edgeExam=1000; comparisons=7697 |
| TopoSort (DAG) | Medium | 100 | 500 | 0.0410 | 500 | valid=true |
| TopoSort (cyclic) | Medium | 100 | 500 | 0.0125 | 0 | cycleDetected=true |
| SCC (Kosaraju) | Medium | 100 | 500 | 0.1016 | 1000 | components=1; largest=100 |
| Dijkstra | Medium | 100 | 500 | 0.0917 | 800 | relaxations=500; pqOps=300; reachable=100 |
| Kruskal | Large | 1000 | 5000 | 0.3914 | 63793 | weight=13123; edgeExam=3356; comparisons=50369; dsuOps=10068 |
| Prim | Large | 1000 | 5000 | 0.4376 | 120958 | weight=13123; edgeExam=10000; comparisons=110958 |
| TopoSort (DAG) | Large | 1000 | 5000 | 0.0963 | 5000 | valid=true |
| TopoSort (cyclic) | Large | 1000 | 5000 | 0.0386 | 54 | cycleDetected=true |
| SCC (Kosaraju) | Large | 1000 | 5000 | 0.5280 | 10000 | components=18; largest=983 |
| Dijkstra | Large | 1000 | 5000 | 0.2439 | 7786 | relaxations=4946; pqOps=2840; reachable=990 |
