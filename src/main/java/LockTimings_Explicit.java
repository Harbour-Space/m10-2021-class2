import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockTimings_Explicit {
    public static void main(String[] args) throws InterruptedException {
        int threadCount = 5;

        CountDownLatch prepare = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);

        // TODO(sandello): Try to change fairness mode here.
        Lock lock = new ReentrantLock(false);
        long deadline = System.nanoTime() + 1_000_000_000;

        Runnable threadMain = () -> {
            prepare.countDown();
            try {
                start.await();
            } catch (InterruptedException ignored) {
            }
            long counter = 0;
            while (System.nanoTime() < deadline) {
                lock.lock();
                ++counter;
                lock.unlock();
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
