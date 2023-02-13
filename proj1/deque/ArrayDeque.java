package deque;

public class ArrayDeque<Item> {
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

    public void resize(int capacity) {
        Item[] a = (Item[]) new Object[capacity];
        for (int i = 0; i < items.length; i += 1) {
            a[i] = this.get(i);
        }
        items = a;
        nextLast = size;
        nextFirst = items.length - 1;
    }

    public void addFirst(Item T) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextFirst] = T;
        nextFirst = (nextFirst - 1 + items.length) % items.length;
        size += 1;
    }

    public void addLast(Item T) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextLast] = T;
        nextLast = (nextLast + 1) % items.length;
        size += 1;
    }
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
    public void printDeque() {
        System.out.print(alliteminstring());
        System.out.println();
    }
    public boolean isEmpty() {
        return size == 0;
    }
    public Item get(int i){
        int index = (nextFirst + 1 + i) % items.length;
        return items[index];
    }
    public int size() {
        return size;
    }
}
