import java.util.concurrent.CountDownLatch;

public class LockTimings_Implicit {
    public static void main(String[] args) throws InterruptedException {
        int threadCount = 5;

        CountDownLatch prepare = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);

        Object monitor = new Object();
        long deadline = System.nanoTime() + 1_000_000_000;

        Runnable threadMain = () -> {
            prepare.countDown();
            try {
                start.await();
            } catch (InterruptedException ignored) {
            }
            long counter = 0;
            while (System.nanoTime() < deadline) {
                synchronized (monitor) {
                    ++counter;
                }
            }

            long id = Thread.currentThread().getId();
            System.out.printf("Thread #%d has acquired the lock %d times.%n", id, counter);
        };

        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; ++i) {
            threads[i] = new Thread(threadMain);
            threads[i].start();
        }

        prepare.await();
        start.countDown();

        for (int i = 0; i < threadCount; ++i) {
            threads[i].join();
        }
    }
}
