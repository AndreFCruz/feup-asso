import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import stuff.*;

public class Main {

    public static void main(String[] args) {
        final ExecutorService executor = Executors.newCachedThreadPool();

        // Create Broker
        Broker<Integer> broker = new Broker<Integer>();

        // Create Publishers and populate registry
        Publisher<Integer> pubA = new IntPublisher();
        int pubAKey = broker.register(pubA);
        executor.submit(pubA);

        Publisher<Integer> pubB = new IntPublisher();
        int pubBKey = broker.register(pubB);

        // Create Subscribers
        Subscriber<Integer> subA = new Subscriber<Integer>();
        int subAKey = broker.register(subA);
        Subscriber<Integer> subB = new Subscriber<Integer>();
        int subBKey = broker.register(subB);
        Subscriber<Integer> subC = new Subscriber<Integer>();
        int subCKey = broker.register(subC);
        Subscriber<Integer> subD = new Subscriber<Integer>();
        int subDKey = broker.register(subD);

        // // Manage subscriptions
        broker.addSubscriber(subAKey, pubAKey);
        broker.addSubscriber(subAKey, pubBKey);
        broker.addSubscriber(subBKey, pubBKey);
        broker.addSubscriber(subCKey, pubAKey);

        executor.submit(subA);
        executor.submit(subB);
        executor.submit(subC);
        // executor.submit(subD);

        ExecutorService brokerExec = Executors.newSingleThreadExecutor();
        brokerExec.execute(broker);

        new Thread(() -> {
            try {
                System.out.println("Trying to block Broker's execution in 5 secs");
                brokerExec.awaitTermination(5000, TimeUnit.MILLISECONDS);
                System.out.println("#...#");
            } catch (InterruptedException e) {
                System.out.println("Interrupted kill-switch Thread, lol");
            }
        }).start();
    }
}
