package deque;

import java.util.Iterator;

public class LinkedListDeque<Item> implements Iterable<Item>, Deque<Item> {
    /* store of sentinel node and the size. */
    private Node sentinel;
    private int size;

    private class Node {
        public Node prev;
        public Item item;
        public Node next;

        public Node(Node p, Item i, Node n) {
            prev = p;
            item = i;
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
    public LinkedListDeque(Item x){
        sentinel = new Node(null, null, null);
        sentinel.next = new Node(sentinel, x, sentinel);
        sentinel.prev = sentinel.next;
        size = 1;
    }
    @Override
    public void addFirst(Item T) {
        Node temp = sentinel.next;
        sentinel.next = new Node(sentinel, T, temp);
        temp.prev = sentinel.next;
        size += 1;
    }
    @Override
    public void addLast(Item T) {
        Node temp = sentinel.prev;
        sentinel.prev = new Node(temp, T, sentinel);
        temp.next = sentinel.prev;
        size += 1;
    }
    @Override
    public Item removeFirst() {
        if (this.isEmpty()) {
            return null;
        }
        Node temp = sentinel.next;
        temp.next.prev = sentinel;
        sentinel.next = temp.next;
        size -= 1;
        return temp.item;
    }
    @Override
    public Item removeLast() {
        if (this.isEmpty()) {
            return null;
        }
        Node temp = sentinel.prev;
        temp.prev.next = sentinel;
        sentinel.prev = temp.prev;
        size -= 1;
        return temp.item;
    }
    @Override
    public Item get(int index) {
        Node n = sentinel.next;
        for(int x = 0; x < index; x += 1) {
            if (n == sentinel) {
                return null;
            }
            n = n.next;
        }
        return n.item;
    }

    private Item finditem(int index, Node lst){
        if (index == 0) {
            return lst.item;
        } else if (lst.next == sentinel) {
            return null;
        }
        return finditem(index - 1, lst.next);
    }

    public Item getRecursive(int index) {
        if (isEmpty()) {
            return null;
        }
        return finditem(index, sentinel.next);
    }

    private String alliteminstring() {
        Node n = sentinel.next;
        String allitem = "";
        while (n != sentinel) {
            allitem = allitem + n.item.toString() + " ";
            n = n.next;
        }
        return allitem;
    }
    @Override
    public void printDeque() {
        System.out.print(alliteminstring());
        System.out.println();
    }
    @Override
    public int size() {
        return size;
    }
    public Iterator<Item> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<Item> {
        private Node wizards;

        public LinkedListDequeIterator() {
            wizards = sentinel.next;
        }

        public boolean hasNext() {
            return wizards != sentinel;
        }

        public Item next() {
            Item ritem = wizards.item;
            wizards = wizards.next;
            return ritem;
        }
    }
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (! (other instanceof Deque)) {
            return false;
        }
        Deque o = ((Deque) other);
        if (o.size() != this.size()) {
            return false;
        }
        for (int i = 0; i < size; i += 1) {
            if(! this.get(i).equals(o.get(i))) {
                return false;
            }
        }
        return true;
    }
}

