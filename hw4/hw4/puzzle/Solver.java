package hw4.puzzle;
import edu.princeton.cs.algs4.MinPQ;

public class Solver {
    MinPQ<SearchNode> searchNodes;

    /** Constructor which solves the puzzle,
     * computing everything necessary for moves() and
     * solution() to not have to solve the problem
     * again. Solves the puzzle using the A* algorithm.
     * Assumes a solution exists.
     */
    public Solver(WorldState initial) {
        searchNodes = new MinPQ<>();
        searchNodes.insert(new SearchNode(initial, 0, null));
    }

    /** Returns the minimum number of moves to solve
     * the puzzle starting at the initial WorldState
     */
    public int moves() {

    }

    /** Returns a sequence of WorldStates from the initial WorldState
     *  to the solution
     */
    public Iterable<WorldState> solution() {

    }

    /** Search node class to the solver */
    private class SearchNode {
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
    }
}
