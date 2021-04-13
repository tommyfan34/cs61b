import edu.princeton.cs.introcs.In;

import java.util.ArrayList;

public class Board {
    private static final int NORTH = 0;
    private static final int SOUTH = 1;
    private static final int WEST = 2;
    private static final int EAST = 3;
    private static final int NORTHWEST = 4;
    private static final int NORTHEAST = 5;
    private static final int SOUTHWEST = 6;
    private static final int SOUTHEAST = 7;

    private char[][] content;
    private boolean[][] visited;
    private int width;
    private int height;

    public Board(String filepath) {
        In input = new In(filepath);
        ArrayList<String> board = new ArrayList<>();
        String s = input.readLine();
        board.add(s);
        int w = s.length();
        while (input.hasNextLine()) {
            s = input.readLine();
            board.add(s);
            if (s.length() != w) {
                throw new IllegalArgumentException("Input board is not rectangular");
            }
        }
        width = board.get(0).length();
        height = board.size();
        content = new char[height][width];
        visited = new boolean[height][width];
        for (int y = 0; y < board.size(); y++) {
            s = board.get(y);
            for (int x = 0; x < s.length(); x++) {
                content[y][x] = s.charAt(x);
                visited[y][x] = false;
            }
        }
    }

    private boolean inRange(int row, int col) {
        return !(row < 0 || row >= height || col < 0 || col >= width);
    }

    public char getElem(Coordinate cor) {
        int row = cor.row;
        int col = cor.col;

        if (!inRange(row, col)) {
            throw new IndexOutOfBoundsException();
        }
        return content[row][col];
    }

    public boolean visited(Coordinate cor) {
        int row = cor.row;
        int col = cor.col;
        if (!inRange(row, col)) {
            throw new IndexOutOfBoundsException();
        }
        return visited[row][col];
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public ArrayList<Coordinate> neighbors(Coordinate cor) {
        ArrayList<Coordinate> ret = new ArrayList<>();
        ArrayList<Coordinate> temp = new ArrayList<>();
        temp.add(new Coordinate(cor.row + 1, cor.col));
        temp.add(new Coordinate(cor.row + 1, cor.col + 1));
        temp.add(new Coordinate(cor.row + 1, cor.col - 1));
        temp.add(new Coordinate(cor.row, cor.col + 1));
        temp.add(new Coordinate(cor.row, cor.col - 1));
        temp.add(new Coordinate(cor.row - 1, cor.col));
        temp.add(new Coordinate(cor.row - 1, cor.col + 1));
        temp.add(new Coordinate(cor.row - 1, cor.col - 1));
        for (Coordinate c : temp) {
            if (inRange(c.row, c.col)) {
                ret.add(c);
            }
        }
        return ret;
    }

    public void visit(Coordinate cor) {
        visited[cor.row][cor.col] = true;
    }

    public void unvisit(Coordinate cor) {
        visited[cor.row][cor.col] = false;
    }

    public void clearVisit() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                visited[row][col] = false;
            }
        }
    }
}
