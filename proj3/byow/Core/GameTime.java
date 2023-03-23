package byow.Core;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class GameTime implements Serializable {
    long PREVTIME;
    long TIMESPEND;
    long LIMIT;

    GameTime(long limit) {
        TIMESPEND = 0;
        this.LIMIT = TimeUnit.NANOSECONDS.convert(limit, TimeUnit.SECONDS);
        PREVTIME = System.nanoTime();
    }

    boolean timesUp() {
        long currentTime = System.nanoTime();
        TIMESPEND += currentTime - PREVTIME;
        PREVTIME = currentTime;
        return TIMESPEND > LIMIT;
    }

    int timesUsed() {
       return (int) TimeUnit.SECONDS.convert(TIMESPEND, TimeUnit.NANOSECONDS);
    }

    void restart() {
        PREVTIME = System.nanoTime();
    }
}
