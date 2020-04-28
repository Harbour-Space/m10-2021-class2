public class Doubler1_Unsynchronized {
    static class Doubler {
        private long input = 0;
        private long output = 0;

        private final Thread worker;

        Doubler() {
            worker = new Thread(this::work);
            worker.setName("Worker");
            worker.start();
        }

        synchronized void provideInput(long value) {
            input = value;
        }

        synchronized long consumeOutput() {
            long tmp = output;
            output = 0;
            return tmp;
        }

        void work() {
            while (!Thread.interrupted()) {
                synchronized (this) {
                    output = input * 2;
                    input = 0;
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
