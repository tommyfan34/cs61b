import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import edu.princeton.cs.algs4.MinPQ;
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
        MinPQ<String> temp = new MinPQ<>(new StringComparator());
        In in = new In(dictPath);
        Trieset trieset = new Trieset();
        while (in.hasNextLine()) {
            trieset.put(in.readLine());
        }
        boolean test = trieset.hasWord("bs");
        Board board = new Board(boardFilePath);

        for (int row = 0; row < board.height(); row++) {
            for (int col = 0; col < board.width(); col++) {
                helperVisit(new Coordinate(row, col), "", trieset.root, trieset, board, temp);
            }
        }

        boolean equals;
        String last = null;
        for (int i = 0; i < k; ) {
            if (temp.isEmpty()) {
                break;
            }
            String s = temp.delMin();
            if (s.equals(last)) {
                equals = true;
            } else {
                equals = false;
            }
            if (!equals) {
                ret.add(s);
                i++;
            }
            last = s;
        }

        return ret;
    }

    private static void helperVisit(Coordinate cor, String s, Trieset.Node node,
                             Trieset trieset, Board board, MinPQ<String> temp) {
        char c = board.getElem(cor);
        node = trieset.getNext(node, c);
        if (node == null) {
            return;
        }
        board.visit(cor);
        s += c;
        if (node.exists) {
            temp.insert(s);
        }
        for (Coordinate neighbor : board.neighbors(cor)) {
            if (!board.visited(neighbor)) {
                helperVisit(neighbor, s, node, trieset, board, temp);
            }
        }
        board.unvisit(cor);
    }

    private static class StringComparator implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            if (s1.length() > s2.length()) {
                return -1;
            } else if (s1.length() < s2.length()) {
                return 1;
            } else {
                return helper(s1, s2);
            }
        }

        private int helper(String s1, String s2) {
            if (s1.length() == 0) {
                return 0;
            }
            if (s1.charAt(0) > s2.charAt(0)) {
                return 1;
            } else if (s1.charAt(0) < s2.charAt(0)) {
                return -1;
            } else {
                return helper(s1.substring(1), s2.substring(1));
            }
        }
    }

    public static void main(String[] args) {
        List<String> result = solve(20, "exampleBoard2.txt");
    }
}
