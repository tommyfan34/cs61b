import java.util.ArrayList;

public class Trieset {
    Node root;

    public Trieset() {
        root = new Node('\0');
    }

    public class Node {
        boolean exists;
        ArrayList<Node> nodes;
        char elem;

        Node(char c) {
            elem = c;
            exists = false;
            nodes = new ArrayList<>();
        }
    }

    public void put(String s) {
        put(s, root);
    }

    private void put(String s, Node n) {
        if (s == null || s.equals("")) {
            return;
        }
        Node next = getNext(n, s.charAt(0));
        if (next == null) {
            next = new Node(s.charAt(0));
            n.nodes.add(next);
        }
        if (s.length() == 1) {
            next.exists = true;
        }
        put(s.substring(1), next);
    }


    public boolean hasWord(String s) {
        return hasWord(s, root);
    }

    private boolean hasWord(String s, Node n) {
        if (s == null || s.equals("")) {
            return false;
        }
        Node next = getNext(n, s.charAt(0));
        if (next == null) {
            return false;
        }
        if (next.exists && s.length() == 1) {
            return true;
        } else {
            return hasWord(s.substring(1), next);
        }
    }

    public Node getNext(Node n, char c) {
        Node ret = null;
        for (Node child : n.nodes) {
            if (Character.toLowerCase(child.elem) == Character.toLowerCase((c))) {
                ret = child;
                return ret;
            }
        }
        return ret;
    }
}
