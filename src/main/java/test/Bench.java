package test;

import java.util.Random;

/**
 * Measures the performance of random number generation
 *
 * @author Kohsuke Kawaguchi
 */
public class Bench implements Runnable {
    private final Random rnd;

    private final byte[] buf = new byte[1024];

    public Bench(Random rnd) {
        this.rnd = rnd;
    }

    public void run() {
        int times = 100;

        System.out.printf("Started\n");
        // warm up
        for (int i=0; i<times; i++) {
            seq();
        }

        // main run
        long[] duration = new long[times];
        for (int i=0; i<times; i++) {
            duration[i] = seq();
        }

        // keep them going to sustain the load for other threads
        for (int i=0; i<times; i++) {
            seq();
        }

        long avg = average(duration);
        System.out.printf("average=%d, stddev=%d\n", avg, stddev(duration,avg));
    }

    private long stddev(long[] duration, long avg) {
        long sum = 0;
        for (long l : duration) {
            sum += (l-avg)*(l-avg);
        }
        return (long)Math.sqrt(sum/duration.length);
    }

    private long average(long[] duration) {
        long total = 0;
        for (long l : duration) {
            total += l;
        }
        return total/duration.length;
    }

    private long seq() {
        long start = System.nanoTime();
        for (int i=0; i<1000;i++) {
            rnd.nextBytes(buf);
        }
        long end = System.nanoTime();

        return end-start;
    }
}
