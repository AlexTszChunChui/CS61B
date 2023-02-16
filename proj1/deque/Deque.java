package deque;

import java.util.Iterator;

public interface Deque<Item> {
    public void addFirst(Item T);
    public void addLast(Item T);
    default public boolean isEmpty() {
        return size() == 0;
    }
    public int size();
    public void printDeque();
    public Item removeFirst();
    public Item removeLast();
    public Item get(int index);

}
