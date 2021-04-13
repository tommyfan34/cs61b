import java.util.ArrayList;
import java.util.List;
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
        ArrayList<String> ret = new ArrayList<>();
        In in = new In(dictPath);
        Trieset trieset = new Trieset();
        while (in.hasNextLine()) {
            trieset.put(in.readLine());
        }
        Board board = new Board(boardFilePath);
        boolean test = trieset.hasWord("b");
        return null;
    }

    public static void main(String[] args) {
        List<String> result = solve(7, "exampleBoard.txt");
    }
}
