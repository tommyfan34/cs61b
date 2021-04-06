package lab11.graphs;

import java.util.ArrayList;

/**
 *  @author Josh Hug
 */
public class MazeAStarPath extends MazeExplorer {
    private int s;
    private int t;
    private boolean targetFound = false;
    private Maze maze;
    private ArrayList<Integer> fringe;

    public MazeAStarPath(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        maze = m;
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        distTo[s] = 0;
        edgeTo[s] = s;
        fringe = new ArrayList<>();
    }

    /** Estimate of the distance from v to the target. */
    private int h(int v) {
        int sourceX = maze.toX(v);
        int sourceY = maze.toY(v);
        int targetX = maze.toX(t);
        int targetY = maze.toY(t);
        return Math.abs(sourceX - targetX) + Math.abs(sourceY - targetY);
    }

    /** Finds vertex estimated to be closest to target. */
    private int findMinimumUnmarked() {
        if (fringe.isEmpty()) {
            return -1;
        }
        int minimum = fringe.get(0);
        int index = 0;
        for (int i = 0; i < fringe.size(); i++) {
            if (h(minimum) > h(fringe.get(i))) {
                minimum = fringe.get(i);
                index = i;
            }
        }
        fringe.remove(index);
        return minimum;
        /* You do not have to use this method. */
    }

    /** Performs an A star search from vertex s. */
    private void astar() {
        int v = s;
        fringe.add(v);
        while (!fringe.isEmpty()) {
            if (v == t) {
                targetFound = true;
            }
            v = findMinimumUnmarked();
            marked[v] = true;
            if (targetFound) {
                announce();
                return;
            }
            for (int u : maze.adj(v)) {
                if (!marked[u]) {
                    fringe.add(u);
                    edgeTo[u] = v;
                    announce();
                    distTo[u] = distTo[v] + 1;

                }
            }
        }
    }

    @Override
    public void solve() {
        astar();
    }

}

