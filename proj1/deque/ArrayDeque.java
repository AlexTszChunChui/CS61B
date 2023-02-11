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
        nextLast = items.length;
        items = a;
        nextFirst = items.length - 1;
    }

    public void addFirst(Item T) {
        if (size == items.length) {
            resize(size * 2);
        } else if (nextFirst == 0) {
            nextFirst = items.length - 1;
        }
        items[nextFirst] = T;
        nextFirst -= 1;
        size += 1;
    }

    public void addLast(Item T) {
        if (size == items.length) {
            resize(size * 2);
        } else if (nextLast == items.length - 1) {
            nextLast = 0;
        }
        items[nextLast] = T;
        nextLast += 1;
        size += 1;
    }
    public Item removeFirst() {
        if (isEmpty()) {
            return null;
        }
        else if (nextFirst == items.length - 1) {
            nextFirst = 0;
        }
        else {
            nextFirst += 1;
        }
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
        else if (nextLast == 0) {
            nextLast = items.length - 1;
        }
        else {
            nextLast -= 1;
        }
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
            if (now == items.length - 1) {
                now = 0;
            }
            else{
                now += 1;
            }
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
