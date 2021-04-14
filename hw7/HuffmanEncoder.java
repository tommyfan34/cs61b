import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HuffmanEncoder {
    public static Map<Character, Integer> buildFrequencyTable(char[] inputSymbols) {
        Map<Character, Integer> ret = new HashMap<>();
        for (char c : inputSymbols) {
            Integer freq = ret.get(c);
            if (freq == null) {
                ret.put(c, 1);
            } else {
                ret.put(c, freq + 1);
            }
        }
        return ret;
    }

    public static void main(String[] args) {
        char[] inputChars = FileUtils.readFile(args[0]);
        String hufFile = args[0] + ".huf";
        Map<Character, Integer> freqTable = buildFrequencyTable(inputChars);
        BinaryTrie trie = new BinaryTrie(freqTable);
        ObjectWriter objectWriter = new ObjectWriter(hufFile);
        objectWriter.writeObject(trie);
        Map<Character, BitSequence> lookupTable = trie.buildLookupTable();
        List<BitSequence> bitSequences = new ArrayList<>();
        for (char c : inputChars) {
            bitSequences.add(lookupTable.get(c));
        }
        BitSequence hugeBitSquence = BitSequence.assemble(bitSequences);
        objectWriter.writeObject(hugeBitSquence);
    }
}
