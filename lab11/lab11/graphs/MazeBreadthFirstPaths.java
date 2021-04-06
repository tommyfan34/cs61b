package lab11.graphs;

import java.util.LinkedList;
import java.util.Queue;

/**
 *  @author Josh Hug
 */
public class MazeBreadthFirstPaths extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */

    private Queue<Integer> fringe;
    private Maze maze;
    private boolean targetFound = false;
    private int s;
    private int t;
    public MazeBreadthFirstPaths(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        // Add more variables here!
        maze = m;
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        fringe = new LinkedList<>();
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    /** Conducts a breadth first search of the maze starting at the source. */
    private void bfs() {
        int v = s;
        fringe.add(v);
        while (v != t && !fringe.isEmpty()) {
            v = fringe.remove();
            marked[v] = true;
            announce();
            for (int w : maze.adj(v)) {
                if (!marked[w]) {
                    edgeTo[w] = v;
                    announce();
                    distTo[w] = distTo[v] + 1;
                    fringe.add(w);
                }
            }
        }
    }


    @Override
    public void solve() {
        bfs();
    }
}

