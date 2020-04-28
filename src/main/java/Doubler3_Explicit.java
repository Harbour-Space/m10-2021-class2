import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Doubler3_Explicit {
    static class Doubler {
        private long input = 0;
        private long output = 0;

        private Lock lock = new ReentrantLock();
        private Condition condition = lock.newCondition();

        private final Thread worker;

        Doubler() {
            worker = new Thread(this::work);
            worker.setName("Worker");
            worker.start();
        }

        void provideInput(long value) throws InterruptedException {
            lock.lock();
            try {
                // Wait for predicate: [input == 0]
                while (input != 0) {
                    condition.await(); // Lock is released when falling asleep.
                }
                // Proceed.
                input = value;
                // Notify that the state has changed.
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }

        long consumeOutput() throws InterruptedException {
            lock.lock();
            try {
                // Wait for predicate: [output != 0]
                while (output == 0) {
                    condition.await(); // Lock is released when falling asleep.
                }
                // Proceed.
                long tmp = output;
                output = 0;
                // Notify that the state has changed.
                condition.signalAll();
                return tmp;
            } finally {
                lock.unlock();
            }
        }

        void work() {
            while (!Thread.interrupted()) {
                lock.lock();
                try {
                    // Wait for predicate: [input != 0]
                    while (input == 0) {
                        try {
                            condition.await(); // Lock is released when falling asleep.
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    // Proceed.
                    output = input * 2;
                    input = 0;
                    // Notify that the state has changed.
                    condition.signalAll();
                } finally {
                    lock.unlock();
                }
            }
        }

        void stop() throws InterruptedException {
            worker.interrupt();
            worker.join();
        }
    }

    private static final int ITERATIONS = 100;

    public static void main(String[] args) throws InterruptedException {
        Doubler d = new Doubler();

        for (int iteration = 0; iteration < ITERATIONS; ++iteration) {
            long x = 42;
            System.out.printf("Working on value %d...%n", x);
            d.provideInput(x);
            long y = d.consumeOutput();
            System.out.printf("Got %d! Hooray!%n", y);
        }

        d.stop();
    }
}
