import edu.princeton.cs.algs4.MinPQ;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BinaryTrie implements Serializable {
    private Node root;

    public BinaryTrie(Map<Character, Integer> frequencyTable) {
        MinPQ<Node> pq = new MinPQ<>();
        Set<Map.Entry<Character, Integer>> chars = frequencyTable.entrySet();
        // initialize priority queue with singleton trees
        for (Map.Entry<Character, Integer> entry : chars) {
            pq.insert(new Node(entry.getKey(), entry.getValue(), null, null));
        }

        // merge two smallest trees
        while (pq.size() > 1) {
            Node left = pq.delMin();
            Node right = pq.delMin();
            Node parent = new Node('\0', left.freq + right.freq, left, right);
            pq.insert(parent);
        }
        root = pq.delMin();
    }

    public Match longestPrefixMatch(BitSequence querySequence) {
        Node current = root;
        Node child = null;
        BitSequence bitSequence = new BitSequence();
        for (int i = 0; i < querySequence.length(); i++) {
            int b = querySequence.bitAt(i);
            if (b == 0) {
                child = current.left;
            } else if (b == 1) {
                child = current.right;
            }
            if (child == null) {
                return new Match(bitSequence, current.ch);
            }
            if (i != querySequence.length()) {
                current = child;
            }
            bitSequence = bitSequence.appended(b);
        }
        return new Match(bitSequence, current.ch);
    }

    public Map<Character, BitSequence> buildLookupTable() {
        Map<Character, BitSequence> ret = new HashMap<>();
        helperBuildLookupTable(root, ret, new BitSequence());
        return ret;
    }

    private void helperBuildLookupTable(Node node, Map<Character, BitSequence> map,
                                        BitSequence sequence) {
        if (node.left != null) {
            helperBuildLookupTable(node.left, map, sequence.appended(0));
        }
        if (node.right != null) {
            helperBuildLookupTable(node.right, map, sequence.appended(1));
        }
        char ch = node.ch;
        if (ch != '\0') {
            map.put(ch, sequence);
        }
    }

    private class Node implements Comparable<Node>, Serializable {
        private final char ch;
        private final int freq;
        private final Node left, right;

        Node(char ch, int freq, Node left, Node right) {
            this.ch = ch;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        @Override
        public int compareTo(Node that) {
            return this.freq - that.freq;
        }
    }

    public char getChar(Node node) {
        return node.ch;
    }

}
