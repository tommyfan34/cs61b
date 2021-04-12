import java.util.ArrayList;
import java.util.LinkedList;

public class Trieset {
    Node root;

    public Trieset() {
        root = new Node('\0');
    }

    private class Node {
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
        if (s == null || s == "") {
            return;
        }
        if (!hasChar(n, s.charAt(0))) {
            Node newNode = new Node(s.charAt(0));
            n.nodes.add(newNode);
            if (s.length() == 1) {
                newNode.exists = true;
            }
            put(s.substring(1), newNode);
        } else {
            Node next = null;
            for (int i = 0; i < n.nodes.size(); i++) {
                if (n.nodes.get(i).elem == s.charAt(0)) {
                    next = n.nodes.get(i);
                    break;
                }
            }
            if (s.length() == 1) {
                next.exists = true;
            }
            put(s.substring(1), next);
        }
    }

    private boolean hasChar(Node n, char c) {
        for (Node child : n.nodes) {
            if (child.elem == c) {
                return true;
            }
        }
        return false;
    }

    public LinkedList<String> StringListwithPrefix(String s) {
        LinkedList<String> ret = new LinkedList<>();
        getListByPrefix("", s, root, ret);
        return ret;
    }

    private void getListByPrefix(String prefix, String next, Node n, LinkedList<String> ret) {
        if (n == null) {
            return;
        }
        if (next == null) {
            return;
        }
        if (next == "") {
            if (n.exists) {
                ret.add(prefix + n.elem);
            }
            prefix += n.elem;
            for (int i = 0; i < n.nodes.size(); i++) {
                getListByPrefix(prefix, "", n.nodes.get(i), ret);
            }
            return;
        } else {
            char c = next.charAt(0);
            for (int i = 0; i < n.nodes.size(); i++) {
                Node child = n.nodes.get(i);
                if (c == Character.toLowerCase(child.elem)) {
                    getListByPrefix(prefix + n.elem, next.substring(1), child, ret);
                } else if (!Character.isLetter(child.elem)) {
                    getListByPrefix(prefix + n.elem, next, child, ret);
                }
            }
        }
    }

    public static void main(String[] args) {
        Trieset Trie = new Trieset();
        Trie.put("Same");
        Trie.put("S' am");
        Trie.put("sap");
        Trie.put("sad");
        Trie.put("a");
        Trie.put("awls");
        LinkedList<String> ret = Trie.StringListwithPrefix("sa");
        System.out.println(ret.get(0));
    }
}
