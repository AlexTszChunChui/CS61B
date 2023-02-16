package deque;


import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {
    private T[] Ts;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        Ts = (T[]) new Object[8];
        size = 0;
        nextFirst = 4;
        nextLast = 5;
    }

    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        for (int i = 0; i < size; i += 1) {
            a[i] = this.get(i);
        }
        Ts = a;
        nextLast = size;
        nextFirst = Ts.length - 1;
    }
    @Override
    public void addFirst(T T) {
        if (T == null) {
            throw new IllegalArgumentException("Not allowed to add null");
        }
        if (size == Ts.length) {
            resize(size * 2);
        }
        Ts[nextFirst] = T;
        nextFirst = (nextFirst - 1 + Ts.length) % Ts.length;
        size += 1;
    }
    @Override
    public void addLast(T T) {
        if (T == null) {
            throw new IllegalArgumentException("Not allowed to add null");
        }
        if (size == Ts.length) {
            resize(size * 2);
        }
        Ts[nextLast] = T;
        nextLast = (nextLast + 1) % Ts.length;
        size += 1;
    }
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        nextFirst = (nextFirst + 1) % Ts.length;
        size -= 1;
        T temp = Ts[nextFirst];
        Ts[nextFirst] = null;
        if ((size < Ts.length / 4) && (size > 4)) {
            resize(Ts.length / 4);
        }
        return temp;
    }
    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        nextLast = (nextLast - 1 + Ts.length) % Ts.length;
        size -= 1;
        T temp = Ts[nextLast];
        Ts[nextLast] = null;
        if ((size < Ts.length / 4) && (size > 4)) {
            resize(Ts.length / 4);
        }
        return temp;
    }

    private String allTinstring() {
        int now = nextFirst + 1;
        String everything = "";
        while (now != nextLast) {
            everything = everything + Ts[now].toString() + " ";
            now = (now + 1) % Ts.length;
        }
        return everything;
    }
    @Override
    public void printDeque() {
        System.out.print(allTinstring());
        System.out.println();
    }
    @Override
    public T get(int i) {
        int index = (nextFirst + 1 + i) % Ts.length;
        return Ts[index];
    }
    @Override
    public int size() {
        return size;
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int position;

        public ArrayDequeIterator() {
            position = 0;
        }

        public boolean hasNext() {
            return position < size;
        }

        public T next() {
            T rT = get(position);
            position += 1;
            return rT;
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
            if (! this.get(i).equals(o.get(i))) {
                return false;
            }
        }
        return true;
    }
}


