package deque;

import java.util.Comparator;

public class MaxArrayDeque<Item> extends ArrayDeque<Item>{
    public Comparator<Item> normalcomparator;
    public MaxArrayDeque(Comparator<Item> c) {
        this.normalcomparator = c;
    }
    public Item max() {
        if (isEmpty()) {
            return null;
        }
        Item max = get(0);
        for (int x = 1; x < size(); x += 1) {
            Item now = get(x);
            if (normalcomparator.compare(now, max) > 0) {
                max = now;
            }
        }
        return max;
    }
    public Item max(Comparator<Item> c) {
        if (isEmpty()) {
            return null;
        }
        Item max = get(0);
        for (int x = 1; x < size(); x += 1) {
            Item now = get(x);
            if (c.compare(now, max) > 0) {
                max = now;
            }
        }
        return max;
    }
}
