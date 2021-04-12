import edu.princeton.cs.algs4.Picture;
import java.util.ArrayList;

public class SeamCarver {
    private Picture pic;
    private double[][] energies;
    public SeamCarver(Picture picture) {
        pic = picture;

    }

    // current picture
    public Picture picture() {
        return pic;
    }

    // width of current picture
    public int width() {
        return pic.width();
    }

    // height of current picture
    public int height() {
        return pic.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || y < 0 || x >= width() || y >= height()) {
            throw new IndexOutOfBoundsException();
        }
        double energyX;
        double energyY;

        energyX = Math.pow(pic.get(incX(x), y).getRed() - pic.get(decX(x), y).getRed(), 2) +
                Math.pow(pic.get(incX(x), y).getGreen() - pic.get(decX(x), y).getGreen(), 2) +
                Math.pow(pic.get(incX(x), y).getBlue() - pic.get(decX(x), y).getBlue(), 2);
        energyY = Math.pow(pic.get(x, incY(y)).getRed() - pic.get(x, decY(y)).getRed(), 2) +
                Math.pow(pic.get(x, incY(y)).getGreen() - pic.get(x, decY(y)).getGreen(), 2) +
                Math.pow(pic.get(x, incY(y)).getBlue() - pic.get(x, decY(y)).getBlue(), 2);
        return energyX + energyY;
    }

    private int findMin(int x, int y) {
        ArrayList<Integer> toCompare = new ArrayList<>();
        if (x == 0) {
            toCompare.add(x);
            toCompare.add(x + 1);
        } else if (x == width() - 1) {
            toCompare.add(x);
            toCompare.add(x - 1);
        } else {
            toCompare.add(x);
            toCompare.add(x - 1);
            toCompare.add(x + 1);
        }
        int index = 0;
        for (int i = 0; i < toCompare.size(); i++) {
            if (energy(toCompare.get(i), y - 1) < energy(toCompare.get(index), y - 1)) {
                index = i;
            }
        }
        return toCompare.get(index);
    }


    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        energies = new double[width()][height()];
        int[][] path = new int[width()][height() - 1];
        int[] ret = new int[height()];
        for (int x = 0; x < width(); x++) {
            energies[x][0] = energy(x, 0);
        }
        if (height() != 1) {
            for (int y = 1; y < height(); y++) {
                for (int x = 0; x < width(); x++) {
                    int index = findMin(x, y);
                    energies[x][y] = energy(x, y) + energies[index][y - 1];
                    path[x][y - 1] = index;
                }
            }
        }

        int index = 0;
        for (int y = height() - 1; y >= 0; y--) {
            if (y == height() - 1) {
                for (int i = 0; i < width(); i++) {
                    if (energies[i][y] < energies[index][y]) {
                        index = i;
                    }
                }
                ret[y] = index;
                index = path[index][y - 1];
            } else {
                ret[y] = index;
                if (y != 0) {
                    index = path[index][y - 1];
                }
            }
        }
        return ret;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        transpose();
        int[] ret = findVerticalSeam();
        transpose();
        return ret;
    }

    // remove horizontal seam from picture
    public void removeHorizontalSeam(int[] seam) {
        pic = SeamRemover.removeHorizontalSeam(pic, seam);
    }

    // remove vertical seam from picture
    public void removeVerticalSeam(int[] seam) {
        pic = SeamRemover.removeVerticalSeam(pic, seam);
    }

    private int incX(int x) {
        if (x == width() - 1) {
            return 0;
        } else {
            return x + 1;
        }
    }

    private int decX(int x) {
        if (x == 0) {
            return width() - 1;
        } else {
            return x - 1;
        }
    }

    private int incY(int y) {
        if (y == height() - 1) {
            return 0;
        } else {
            return y + 1;
        }
    }

    private int decY(int y) {
        if (y == 0) {
            return height() - 1;
        } else {
            return y - 1;
        }
    }

    private void transpose() {
        Picture newPic = new Picture(height(), width());
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                newPic.setRGB(y, x, pic.getRGB(x, y));
            }
        }
        pic = newPic;
    }
}
