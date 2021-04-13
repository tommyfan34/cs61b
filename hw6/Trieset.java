import edu.princeton.cs.algs4.TrieSET;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;

public class Trieset {
    private Node root;
    private Node next;

    public Trieset() {
        root = new Node('\0');
    }

    private class Node {
        boolean exists;
        ArrayList<Node> nodes;
        char elem;

        Node (char c) {
            elem = c;
            exists = false;
            nodes = new ArrayList<>();
        }
    }

    public void put(String s) {
        put(s, root);
    }

    private void put(String s, Node n) {
        if (s == null || s == "") {
            return;
        }
        next = null;
        if (!hasChar(n, s.charAt(0))) {
            next = new Node(s.charAt(0));
            n.nodes.add(next);
        }
        if (s.length() == 1) {
            next.exists = true;
        }
        put(s.substring(1), next);
    }

    private boolean hasChar(Node n, char c) {
        for (Node child : n.nodes) {
            if (Character.toLowerCase(child.elem) == Character.toLowerCase((c))) {
                next = child;
                return true;
            }
        }
        return false;
    }

    public boolean hasWord(String s) {
        return hasWord(s, root);
    }

    private boolean hasWord(String s, Node n) {
        if (s == null || s == "") {
            return false;
        }
        next = null;
        if (!hasChar(n, s.charAt(0))) {
            return false;
        }
        if (next.exists && s.length() == 1) {
            return true;
        } else {
            return hasWord(s.substring(1), next);
        }
    }
}
