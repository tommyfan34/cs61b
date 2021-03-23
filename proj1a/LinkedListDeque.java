public class LinkedListDeque<T> {
    private class Node {
        private T item;
        private Node next;
        private Node prev;

        Node(T i, Node p, Node n) {
            item = i;
            prev = p;
            next = n;
        }
    }

    private int size;
    private Node sentinel;

    /** Creates an empty linked list deque */
    public LinkedListDeque() {
        size = 0;
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
    }

    /** Add an item of type T to the front of the deque */
    public void addFirst(T item) {
        Node toAssert = new Node(item, sentinel, sentinel.next);
        sentinel.next.prev = toAssert;
        sentinel.next = toAssert;
        size += 1;
    }

    /** Add an item of type T to the back of the deque */
    public void addLast(T item) {
        Node toAssert = new Node(item, sentinel.prev, sentinel);
        sentinel.prev.next = toAssert;
        sentinel.prev = toAssert;
        size += 1;
    }

    /** Returns true if deque is empty, false otherwise */
    public boolean isEmpty() {
        return (size == 0);
    }

    /** Returns the number of items in the deque
     *  Must take constant time */
    public int size() {
        return size;
    }

    /** Prints the items in the deque from first to last, seperated by a space */
    public void printDeque() {
        Node ptr = sentinel.next;
        while (ptr != sentinel) {
            System.out.print(ptr.item);
            if (ptr.next != sentinel) {
                System.out.print(" ");
            }
            ptr = ptr.next;
        }
        System.out.println();
    }

    /** Removes and returns the item at the front of the deque.
     *  If no such item exists, returns null */
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T ret = sentinel.next.item;
        sentinel.next.next.prev = sentinel;
        sentinel.next = sentinel.next.next;
        size -= 1;
        return ret;
    }

    /** Removes and returns the item at the back of the deque.
     *  If no such item exists returns null*/
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T ret = sentinel.prev.item;
        sentinel.prev.prev.next = sentinel;
        sentinel.prev = sentinel.prev.prev;
        size -= 1;
        return ret;
    }

    /** Gets the item at the given index, where 0 is the front,
     *  1 is the next item, and so forth.
     *  If no such item exists, returns null. Must not alter the deque.
     *  Use iteration
     */
    public T get(int index) {
        Node ptr = sentinel.next;
        for (int i = 0; i != index; i++) {
            ptr = ptr.next;
        }
        return ptr.item;
    }

    private Node helperGetRecursive(int index, Node n) {
        if (index == 0) {
            return n;
        } else {
            return helperGetRecursive(index - 1, n.next);
        }
    }

    /** Get that uses recursion */
    public T getRecursive(int index) {
        return helperGetRecursive(index, sentinel.next).item;
    }
}
