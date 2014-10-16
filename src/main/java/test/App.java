package test;

import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) throws Exception {
        new App().main();
    }

    public void main() throws Exception {
        singleThreadBench();
        multiThreadMultiInstanceBench(2);
        multiThreadMultiInstanceBench(4);
    }

    private void singleThreadBench() throws Exception {
        new Bench(createRandom()).run();
    }

    private Random createRandom() throws Exception {
//        SecureRandom sr = new SecureRandom();

//        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
//        return inspect(sr);

        byte[] seed = new byte[16];
        new SecureRandom().nextBytes(seed);
        return new AESCounterRNG(seed);
    }

    private void multiThreadMultiInstanceBench(int size) throws Exception {
        List<Runnable> all = new ArrayList<Runnable>();
        for (int i=0; i< size; i++)
            all.add(new Bench(createRandom()));
        runAll(all);
    }

    private void runAll(Collection<Runnable> all) throws Exception {
        ExecutorService es = Executors.newFixedThreadPool(all.size());
        for (Runnable a : all) {
            es.execute(a);
        }
        es.shutdown();
        es.awaitTermination(3, TimeUnit.MINUTES);
    }

    private SecureRandom inspect(SecureRandom s) throws Exception {
        Field f = s.getClass().getDeclaredField("secureRandomSpi");
        f.setAccessible(true);
        Object spi = f.get(s);
        System.out.printf("Provider=%s, SPI=%s\n", s.getProvider(), spi);

        return s;
    }
}
