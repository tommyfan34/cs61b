package hw4.puzzle;

import edu.princeton.cs.algs4.Queue;

public class Board implements WorldState {
    private int[][] tiles;
    private int N;
    private final int BLANK = 0;
    private int[][] goal;
    /** Constructs a board from an N-by-N array of
     *  tiles where tiles[i][j] = tile at row i, column j
     * @param tiles
     */
    public Board(int[][] tiles) {
        N = tiles[0].length;
        this.tiles = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                this.tiles[i][j] = tiles[i][j];
            }
        }
        goal = new int[N][N];
        initializeGoal();
    }

    /** Returns value of tile at row i, column j
     *
     * @param i
     * @param j
     * @return
     */
    public int tileAt(int i, int j) {
        if (i < 0 || i >= N || j < 0 || j >= N) {
            throw new IndexOutOfBoundsException("argument for tileAt out of bounds");
        }
        return tiles[i][j];
    }

    /** Returns the board size N
     *
     * @return
     */
    public int size() {
        return N;
    }

    /** Returns the neighbors of the current board
     *
     * @return
     * @source http://joshh.ug/neighbors.html
     */
    @Override
    public Iterable<WorldState> neighbors() {
        Queue<WorldState> neighbors = new Queue<>();
        // find the blank
        int blankX = -1;
        int blankY = -1;
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                if (tileAt(i, j) == BLANK) {
                    blankX = i;
                    blankY = j;
                }
            }
        }
        int[][] newBoard = new int[size()][size()];
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                newBoard[i][j] = tileAt(i, j);
            }
        }
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                if (Math.abs(i - blankX) + Math.abs(j - blankY) == 1) {
                    newBoard[blankX][blankY] = newBoard[i][j];
                    newBoard[i][j] = BLANK;
                    neighbors.enqueue(new Board(newBoard));
                    newBoard[i][j] = newBoard[blankX][blankY];
                    newBoard[blankX][blankY] = BLANK;
                }
            }
        }
        return neighbors;
    }

    /** Hamming estimate
     *
     * @return
     */
    public int hamming() {
        int ret = 0;
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                if (tileAt(i, j) != goal[i][j]) {
                    ret++;
                }
            }
        }
        return ret;
    }

    /** Manhattan esitmate
     *
     * @return
     */
    public int manhattan() {
        int ret = 0;
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                int val = tileAt(i, j);
                int gX;
                int gY;
                if (val != BLANK) {
                    gX = (val - 1) / N;
                    gY = (val - 1) % N;
                } else {
                    gX = N - 1;
                    gY = N - 1;
                }
                ret += (Math.abs(i - gX) + Math.abs(j - gY));
            }
        }
        return ret;
    }

    /** Estimated distance to goal. This method should
     *  simply return the results of manhattan() when submitted
     *  to Gradescope
     * @return
     */
    @Override
    public int estimatedDistanceToGoal() {
        return manhattan();
    }

    /** Returns true if this board's tile values are the
     *  same position as y's
     * @param y
     * @return
     */
    public boolean equals(Object y) {
        if (y == null) {
            return false;
        }
        if (y instanceof Board) {
            Board b = (Board) y;
            if (b.size() != size()) {
                return false;
            }
            for (int i = 0; i < size(); i++) {
                for (int j = 0; j < size(); j++) {
                    if (tileAt(i, j) != b.tileAt(i, j)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /** Returns the string representation of the board. 
      * Uncomment this method. */
    public String toString() {
        StringBuilder s = new StringBuilder();
        int N = size();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tileAt(i, j)));
            }
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }

    /** initialize the goal board */
    private void initializeGoal() {
        int num = 1;
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                if (i != size() - 1 && j != size() - 1) {
                    goal[i][j] = num;
                    num++;
                } else {
                    goal[i][j] = BLANK;
                }
            }
        }
    }
}
