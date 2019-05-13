import stuff.Broker;
import stuff.IntPublisher;
import stuff.Publisher;
import stuff.Subscriber;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        final ExecutorService executor = Executors.newCachedThreadPool();

        // Create Broker
        Broker<Integer> broker = new Broker<>();

        // Create Publishers and populate registry
        Publisher<Integer> pubA = new IntPublisher();
        int pubAKey = broker.addPublisher(pubA);
        executor.submit(pubA);

        Publisher<Integer> pubB = new IntPublisher();
        int pubBKey = broker.addPublisher(pubB);

        // Create Subscribers
        Subscriber<Integer> subsA = new Subscriber<>(1);
        Subscriber<Integer> subsB = new Subscriber<>(2);
        Subscriber<Integer> subsC = new Subscriber<>(3);
        Subscriber<Integer> subsD = new Subscriber<>(4);

        // // Manage subscriptions
        broker.addSubscriber(subsA, pubAKey);
//        broker.addSubscriber(subsA, pubBKey);
        broker.addSubscriber(subsB, pubBKey);
        broker.addSubscriber(subsC, pubAKey);

        executor.submit(subsA);
        executor.submit(subsB);
        executor.submit(subsC);

        ExecutorService brokerExec = Executors.newSingleThreadExecutor();
        brokerExec.execute(broker);

        new Thread(() -> {
            try {
                brokerExec.awaitTermination(5000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                System.out.println("Interrupted kill-switch Thread, lol");
            }
        }).start();
    }
}
