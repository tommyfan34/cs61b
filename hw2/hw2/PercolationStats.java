package hw2;

import edu.princeton.cs.introcs.StdRandom;
import edu.princeton.cs.introcs.StdStats;


public class PercolationStats {
    private double[] percThresholds;
    int T;

    // perform T independent experiments on an N-by-N grid
    public PercolationStats(int N, int T, PercolationFactory pf) {
        if (N <= 0 || T <= 0) {
            throw new IllegalArgumentException();
        }
        this.T = T;
        percThresholds = new double[T];
        for (int i = 0; i < T; i++) {
            Percolation perc = pf.make(N);
            int n = 0;
            while (true) {
                int row = StdRandom.uniform(N);
                int col = StdRandom.uniform(N);
                if (perc.isOpen(row, col)) {
                    perc.open(row, col);
                    n++;
                }
                if (perc.percolates()) {
                    break;
                }
            }
            percThresholds[i] = n;
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        double ret = StdStats.mean(percThresholds);
        return ret;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        double ret = StdStats.stddev(percThresholds);
        return ret;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLow() {
        double ret = (mean() - 1.96 * stddev() / Math.sqrt(T));
        return ret;
    }

    // high endpoint of 95% confidence interval
    public double confidenceHigh() {
        double ret = (mean() + 1.96 * stddev() / Math.sqrt(T));
        return ret;
    }
}
