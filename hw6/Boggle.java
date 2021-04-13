import java.util.ArrayList;
import java.util.List;

import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.introcs.In;

public class Boggle {
    
    // File path of dictionary file
    static String dictPath = "words.txt";

    /**
     * Solves a Boggle puzzle.
     * 
     * @param k The maximum number of words to return.
     * @param boardFilePath The file path to Boggle board file.
     * @return a list of words found in given Boggle board.
     *         The Strings are sorted in descending order of length.
     *         If multiple words have the same length,
     *         have them in ascending alphabetical order.
     */
    public static List<String> solve(int k, String boardFilePath) {
        if (k <= 0) {
            throw new IllegalArgumentException("k is non positive");
        }
        ArrayList<String> ret = new ArrayList<>();
        In in = new In(dictPath);
        Trieset trieset = new Trieset();
        while (in.hasNextLine()) {
            trieset.put(in.readLine());
        }
        boolean test = trieset.hasWord("bs");
        Board board = new Board(boardFilePath);

        for (int row = 0; row < board.height(); row++) {
            for (int col = 0; col < board.width(); col++) {
                helperVisit(new Coordinate(row, col), "", trieset.root, trieset, board, ret);
            }
        }

        return ret;
    }

    private static void helperVisit(Coordinate cor, String s, Trieset.Node node,
                             Trieset trieset, Board board, ArrayList<String> ret) {
        char c = board.getElem(cor);
        node = trieset.getNext(node, c);
        if (node == null) {
            return;
        }
        board.visit(cor);
        s += c;
        if (node.exists) {
            ret.add(s);
        }
        for (Coordinate neighbor : board.neighbors(cor)) {
            if (!board.visited(neighbor)) {
                helperVisit(neighbor, s, node, trieset, board, ret);
            }
        }
        board.unvisit(cor);
    }

    public static void main(String[] args) {
        List<String> result = solve(7, "exampleBoard.txt");
    }
}
