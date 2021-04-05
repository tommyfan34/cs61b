package hw4.puzzle;
import edu.princeton.cs.algs4.MinPQ;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

public class Solver {
    private MinPQ<SearchNode> searchNodes;
    private int moves;
    private ArrayList<WorldState> solution;

    /** Constructor which solves the puzzle,
     * computing everything necessary for moves() and
     * solution() to not have to solve the problem
     * again. Solves the puzzle using the A* algorithm.
     * Assumes a solution exists.
     */
    public Solver(WorldState initial) {
        searchNodes = new MinPQ<>(new SearchNodeComparator());
        solution = new ArrayList<>();
        searchNodes.insert(new SearchNode(initial, 0, null));

        ArrayList<SearchNode> temp = new ArrayList<>();
        SearchNode x;
        // A* algorithm
        while (true) {
            x = searchNodes.delMin();
            temp.add(x);
            if (x.ws.isGoal()) {
                moves = x.initialToThis;
                break;
            }
            for (WorldState worldState : x.ws.neighbors()) {
                if (x.prev != null && worldState.equals(x.prev.ws)) {
                    continue;
                }
                searchNodes.insert(new SearchNode(worldState, x.initialToThis + 1, x));
            }
        }
        for (int i = 0; i <= moves; i++) {
            solution.add(x.ws);
            x = x.prev;
        }
        Collections.reverse(solution);
    }

    /** Returns the minimum number of moves to solve
     * the puzzle starting at the initial WorldState
     */
    public int moves() {
        return moves;
    }

    /** Returns a sequence of WorldStates from the initial WorldState
     *  to the solution
     */
    public Iterable<WorldState> solution() {
        return solution;
    }

    /** Search node class to the solver */
    private class SearchNode implements Comparable<SearchNode> {
        // current world state
        WorldState ws;
        // the number of moves made to reach
        // this world state from the initial state
        int initialToThis;
        // reference to the previous search node
        SearchNode prev;

        SearchNode(WorldState ws, int initialToThis, SearchNode prev) {
            this.ws = ws;
            this.initialToThis = initialToThis;
            this.prev = prev;
        }

        @Override
        public int compareTo(SearchNode other) {
            return ws.estimatedDistanceToGoal() + initialToThis -
                    other.ws.estimatedDistanceToGoal() - other.initialToThis;
        }
    }

    private class SearchNodeComparator implements Comparator<SearchNode> {
        @Override
        public int compare(SearchNode s1, SearchNode s2) {
            return s1.ws.estimatedDistanceToGoal() + s1.initialToThis
                    - s2.ws.estimatedDistanceToGoal() - s2.initialToThis;
        }
    }
}
