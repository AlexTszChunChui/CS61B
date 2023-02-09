package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }
    public static SLList countingGetLast(int n){
        int time = 0;
        SLList<Integer> test = new SLList<>();
        while (time < n) {
            test.addLast(time);
            time += 1;
        }
        return test;
    }
    public static void timeGetLast() {
        AList<Integer> Ns = new AList<>();
        AList<Double> Times = new AList<>();
        AList<Integer> counts = new AList<>();
        int[] n = {1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000};
        int M = 10000;
        for (int x : n) {
            SLList<Integer> test = countingGetLast(x);
            Stopwatch sw = new Stopwatch();
            for (int z = 0; z < M; z += 1) {
                test.getLast();
            }
            double timeInSeconds = sw.elapsedTime();
            Ns.addLast(x);
            counts.addLast(M);
            Times.addLast(timeInSeconds);
        }
        printTimingTable(Ns, Times, counts);
    }
}
