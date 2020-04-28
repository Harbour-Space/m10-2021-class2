import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class Semaphore_Example {
    public static void main(String[] args) throws InterruptedException {
        Semaphore sema = new Semaphore(2);

        Set<String> onDuty = new HashSet<>();

        Runnable threadMain = () -> {
            String name = Thread.currentThread().getName();
            int iterationLimit = ThreadLocalRandom.current().nextInt(10, 20);
            for (int iteration = 0; iteration < iterationLimit; ++iteration) {
                boolean acquired = false;
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(1000));
                    sema.acquire();
                    acquired = true;
                    synchronized (onDuty) {
                        onDuty.add(name);
                        System.out.printf("%s is on the duty! %s%n", name, onDuty.toString());
                    }
                    Thread.sleep(ThreadLocalRandom.current().nextInt(1000));
                    synchronized (onDuty) {
                        onDuty.remove(name);
                        System.out.printf("%s is off the duty! %s%n", name, onDuty.toString());
                    }
                } catch (InterruptedException ignored) {
                    return;
                } finally {
                    if (acquired) {
                        sema.release();
                    }
                }
            }
        };

        Thread alice = new Thread(threadMain, "Alice");
        Thread bob = new Thread(threadMain, "Bob");
        Thread charlie = new Thread(threadMain, "Charlie");
        Thread dave = new Thread(threadMain, "Dave");

        alice.start();
        bob.start();
        charlie.start();
        dave.start();

        alice.join();
        bob.join();
        charlie.join();
        dave.join();
    }
}
