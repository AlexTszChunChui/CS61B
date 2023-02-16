package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T>{
    public Comparator<T> normalcomparator;
    public MaxArrayDeque(Comparator<T> c) {
        this.normalcomparator = c;
    }
    public T max() {
        if (isEmpty()) {
            return null;
        }
        T max = get(0);
        for (int x = 1; x < size(); x += 1) {
            T now = get(x);
            if (normalcomparator.compare(now, max) > 0) {
                max = now;
            }
        }
        return max;
    }
    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T max = get(0);
        for (int x = 1; x < size(); x += 1) {
            T now = get(x);
            if (c.compare(now, max) > 0) {
                max = now;
            }
        }
        return max;
    }
}
