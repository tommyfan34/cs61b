import edu.princeton.cs.introcs.In;

import java.util.ArrayList;

public class Board {
    private char[][] content;
    boolean[][] visited;
    int width;
    int height;

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


}
