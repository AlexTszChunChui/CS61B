package deque;

import java.util.Comparator;
import java.util.Iterator;

public class ArrayDeque<Item> implements Iterable<Item>, Deque<Item> {
    private Item[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (Item[]) new Object[8];
        size = 0;
        nextFirst = 4;
        nextLast = 5;
    }

    private void resize(int capacity) {
        Item[] a = (Item[]) new Object[capacity];
        for (int i = 0; i < size; i += 1) {
            a[i] = this.get(i);
        }
        items = a;
        nextLast = size;
        nextFirst = items.length - 1;
    }
    @Override
    public void addFirst(Item T) {
        if (T == null) {
            throw new IllegalArgumentException("Not allowed to add null");
        }
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextFirst] = T;
        nextFirst = (nextFirst - 1 + items.length) % items.length;
        size += 1;
    }
    @Override
    public void addLast(Item T) {
        if (T == null) {
            throw new IllegalArgumentException("Not allowed to add null");
        }
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextLast] = T;
        nextLast = (nextLast + 1) % items.length;
        size += 1;
    }
    @Override
    public Item removeFirst() {
        if (isEmpty()) {
            return null;
        }
        nextFirst = (nextFirst + 1) % items.length;
        size -= 1;
        Item temp = items[nextFirst];
        items[nextFirst] = null;
        if ((size < items.length / 4) && (size > 4)) {
            resize(items.length / 4);
        }
        return temp;
    }
    @Override
    public Item removeLast() {
        if (isEmpty()) {
            return null;
        }
        nextLast = (nextLast - 1 + items.length) % items.length;
        size -= 1;
        Item temp = items[nextLast];
        items[nextLast] = null;
        if ((size < items.length / 4) && (size > 4)) {
            resize(items.length / 4);
        }
        return temp;
    }

    private String alliteminstring() {
        int now = nextFirst + 1;
        String everything = "";
        while (now != nextLast) {
            everything = everything + items[now].toString() + " ";
            now = (now + 1) % items.length;
        }
        return everything;
    }
    @Override
    public void printDeque() {
        System.out.print(alliteminstring());
        System.out.println();
    }
    @Override
    public Item get(int i) {
        int index = (nextFirst + 1 + i) % items.length;
        return items[index];
    }
    @Override
    public int size() {
        return size;
    }

    public Iterator<Item> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<Item> {
        private int position;

        public ArrayDequeIterator() {
            position = 0;
        }

        public boolean hasNext() {
            return position < size;
        }

        public Item next() {
            Item ritem = get(position);
            position += 1;
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


