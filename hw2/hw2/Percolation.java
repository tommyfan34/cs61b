package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private int N;
    private WeightedQuickUnionUF fullSet;
    private boolean[] openSet;
    private int openNum = 0;

    // create N-by-N grid, with all sites initially blocked
    public Percolation(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException();
        }
        this.N = N;
        fullSet = new WeightedQuickUnionUF(N * N + 1);  // the last one is the always full sentinel
        openSet = new boolean[N * N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                openSet[corToIndex(i, j)] = false;
            }
        }
    }

    // open the site (row, col) if it is not open
    public void open(int row, int col) {
        if (row < 0 || col < 0 || row >= N || col >= N) {
            throw new IndexOutOfBoundsException();
        }
        openSet[corToIndex(row, col)] = true;
        openNum++;
        // open the top row
        if (row == 0) {
            fullSet.union(corToIndex(row, col), N * N);
        }
        joinAround(row, col);
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (row < 0 || col < 0 || row >= N || col >= N) {
            throw new IndexOutOfBoundsException();
        }
        return openSet[corToIndex(row, col)];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (row < 0 || col < 0 || row >= N || col >= N) {
            throw new IndexOutOfBoundsException();
        }
        return fullSet.connected(corToIndex(row, col), N * N);
    }

    // number of open sites
    public int numberOfOpenSites() {
        return openNum;
    }

    // does the system percolate?
    public boolean percolates() {
        for (int i = 0; i < N; i++) {
            if (isFull(N - 1, i)) {
                return true;
            }
        }
        return false;
    }

    // use for unit testing (not required)
    public static void main(String[] agrs) {

    }

    private int corToIndex(int row, int col) {
        return row * N + col;
    }

    private void joinAround(int row, int col) {
        if (row - 1 >= 0 && isOpen(row - 1, col)) {
            fullSet.union(corToIndex(row, col), corToIndex(row - 1, col));
        }
        if (row + 1 < N && isOpen(row + 1, col)) {
            fullSet.union(corToIndex(row, col), corToIndex(row + 1, col));
        }
        if (col - 1 >= 0 && isOpen(row, col - 1)) {
            fullSet.union(corToIndex(row, col), corToIndex(row, col - 1));
        }
        if (col + 1 < N && isOpen(row, col + 1)) {
            fullSet.union(corToIndex(row, col), corToIndex(row, col + 1));
        }
    }
}
