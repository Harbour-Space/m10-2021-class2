import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class CountDownLatch_Example {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch prepare = new CountDownLatch(4);
        CountDownLatch start = new CountDownLatch(1);

        long startMillis = System.currentTimeMillis();

        Runnable threadMain = () -> {
            try {
                String name = Thread.currentThread().getName();
                String[] ingredients = {"eggs", "milk", "butter", "bread"};
                int index = (int) (Thread.currentThread().getId() % ingredients.length);
                long now;
                now = System.currentTimeMillis() - startMillis;
                System.out.printf("[+%04d] %s went out for %s.%n", now, name, ingredients[index]);
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000));
                now = System.currentTimeMillis() - startMillis;
                System.out.printf("[+%04d] %s is ready!%n", now, name);
                prepare.countDown();
                start.await();
                now = System.currentTimeMillis() - startMillis;
                System.out.printf("[+%04d] %s is running!%n", now, name);
            } catch (InterruptedException ignored) {
            }
        };

        Thread alice = new Thread(threadMain);
        alice.setName("Alice");
        alice.start();
        Thread bob = new Thread(threadMain);
        bob.setName("Bob");
        bob.start();
        Thread charlie = new Thread(threadMain);
        charlie.setName("Charlie");
        charlie.start();
        Thread dave = new Thread(threadMain);
        dave.setName("Dave");
        dave.start();

        long now;
        prepare.await();
        now = System.currentTimeMillis() - startMillis;
        System.out.printf("[+%04d] Everybody is ready...%n", now);
        Thread.sleep(ThreadLocalRandom.current().nextInt(1000));
        now = System.currentTimeMillis() - startMillis;
        System.out.printf("[+%04d] Go!%n", now);

        start.countDown();

        alice.join();
        bob.join();
        charlie.join();
        dave.join();
    }
}
