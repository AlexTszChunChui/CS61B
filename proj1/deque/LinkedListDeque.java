package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {
    /* store of sentinel node and the size. */
    private Node sentinel;
    private int size;

    private class Node {
        private Node prev;
        private T T;
        private Node next;

        Node(Node p, T i, Node n) {
            prev = p;
            T = i;
            next = n;
        }
    }
    /* Create an empty List */
    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    @Override
    public void addFirst(T T) {
        Node temp = sentinel.next;
        sentinel.next = new Node(sentinel, T, temp);
        temp.prev = sentinel.next;
        size += 1;
    }
    @Override
    public void addLast(T T) {
        Node temp = sentinel.prev;
        sentinel.prev = new Node(temp, T, sentinel);
        temp.next = sentinel.prev;
        size += 1;
    }
    @Override
    public T removeFirst() {
        if (this.isEmpty()) {
            return null;
        }
        Node temp = sentinel.next;
        temp.next.prev = sentinel;
        sentinel.next = temp.next;
        size -= 1;
        return temp.T;
    }
    @Override
    public T removeLast() {
        if (this.isEmpty()) {
            return null;
        }
        Node temp = sentinel.prev;
        temp.prev.next = sentinel;
        sentinel.prev = temp.prev;
        size -= 1;
        return temp.T;
    }
    @Override
    public T get(int index) {
        Node n = sentinel.next;
        for (int x = 0; x < index; x += 1) {
            if (n == sentinel) {
                return null;
            }
            n = n.next;
        }
        return n.T;
    }

    private T findT(int index, Node lst) {
        if (index == 0) {
            return lst.T;
        } else if (lst.next == sentinel) {
            return null;
        }
        return findT(index - 1, lst.next);
    }

    public T getRecursive(int index) {
        if (isEmpty()) {
            return null;
        }
        return findT(index, sentinel.next);
    }

    private String allTinstring() {
        Node n = sentinel.next;
        String allT = "";
        while (n != sentinel) {
            allT = allT + n.T.toString() + " ";
            n = n.next;
        }
        return allT;
    }
    @Override
    public void printDeque() {
        System.out.print(allTinstring());
        System.out.println();
    }
    @Override
    public int size() {
        return size;
    }
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        private Node wizards;

        LinkedListDequeIterator() {
            wizards = sentinel.next;
        }

        public boolean hasNext() {
            return wizards != sentinel;
        }

        public T next() {
            T rT = wizards.T;
            wizards = wizards.next;
            return rT;
        }
    }
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof Deque)) {
            return false;
        }
        Deque o = ((Deque) other);
        if (o.size() != this.size()) {
            return false;
        }
        for (int i = 0; i < size; i += 1) {
            if (!this.get(i).equals(o.get(i))) {
                return false;
            }
        }
        return true;
    }
}

