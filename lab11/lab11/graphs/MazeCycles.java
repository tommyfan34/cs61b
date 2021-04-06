package lab11.graphs;

import edu.princeton.cs.algs4.Stack;

/**
 *  @author Josh Hug
 */
public class MazeCycles extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */

    private int[] edgeToCopy;
    public MazeCycles(Maze m) {
        super(m);
    }

    @Override
    public void solve() {
        edgeToCopy = new int[maze.V()];
        // start from (1, 1)
        int v = maze.xyTo1D(1, 1);
        Stack<Integer> fringe = new Stack<>();
        fringe.push(v);
        boolean foundCycle = false;
        while (!fringe.isEmpty()) {
            if (foundCycle) {
                break;
            }
            v = fringe.pop();
            marked[v] = true;
            announce();
            for (int u : maze.adj(v)) {
                if (marked[u] && edgeToCopy[v] != u) {
                    edgeToCopy[u] = v;
                    drawCycles(u);
                    foundCycle = true;
                    break;
                } else if (!marked[u]) {
                    edgeToCopy[u] = v;
                    fringe.push(u);
                }
            }
        }
    }

    // Helper methods go here
    private void drawCycles(int start) {
        int pointer = start;
        while (true) {
            edgeTo[pointer] = edgeToCopy[pointer];
            pointer = edgeToCopy[pointer];
            if (pointer == start) {
                break;
            }
        }
        announce();
    }
}

