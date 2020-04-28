public class Doubler2_Implicit {
    static class Doubler {
        private long input = 0;
        private long output = 0;

        private final Thread worker;

        Doubler() {
            worker = new Thread(this::work);
            worker.setName("Worker");
            worker.start();
        }

        synchronized void provideInput(long value) throws InterruptedException {
            // Wait for predicate: [input == 0]
            while (input != 0) {
                this.wait(); // Lock is released when falling asleep.
            }
            // Proceed.
            input = value;
            // Notify that the state has changed.
            this.notifyAll();
        }

        synchronized long consumeOutput() throws InterruptedException {
            // Wait for predicate: [output != 0]
            while (output == 0) {
                wait(); // Lock is released when falling asleep.
            }
            // Proceed.
            long tmp = output;
            output = 0;
            // Notify that the state has changed.
            notifyAll();
            return tmp;
        }

        void work() {
            while (!Thread.interrupted()) {
                synchronized (this) {
                    // Wait for predicate: [input != 0]
                    while (input == 0) {
                        try {
                            wait(); // Lock is released when falling asleep.
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    // Proceed.
                    output = input * 2;
                    input = 0;
                    // Notify that the state has changed.
                    notifyAll();
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
