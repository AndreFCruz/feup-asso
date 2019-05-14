import stuff.Broker;
import stuff.Publisher;
import stuff.Subscriber;
import stuff.implementations.ConcretePublisher;
import stuff.implementations.ConcreteSubscriber;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        final ExecutorService executor = Executors.newCachedThreadPool();

        // Create Broker
        Broker<Integer> broker = new Broker<>();

        // Create Publishers and populate registry
        Publisher<Integer> pubA = new ConcretePublisher();
        int pubAKey = broker.register(pubA);
        executor.submit(pubA);

        Publisher<Integer> pubB = new ConcretePublisher();
        int pubBKey = broker.register(pubB);
        executor.submit(pubB);

        // Create Subscribers
        Subscriber<Integer> subA = new ConcreteSubscriber();
        int subAKey = broker.register(subA);
        Subscriber<Integer> subB = new ConcreteSubscriber();
        int subBKey = broker.register(subB);
        Subscriber<Integer> subC = new ConcreteSubscriber();
        int subCKey = broker.register(subC);
        Subscriber<Integer> subD = new ConcreteSubscriber();
        int subDKey = broker.register(subD);

        // // Manage subscriptions
        broker.addSubscriber(subAKey, pubAKey);
        broker.addSubscriber(subAKey, pubBKey);
        broker.addSubscriber(subBKey, pubBKey);
        broker.addSubscriber(subCKey, pubAKey);
        broker.addSubscriber(subDKey, pubAKey);

        executor.submit(subA);
        executor.submit(subB);
        executor.submit(subC);
        executor.submit(subD);

        ExecutorService brokerExec = Executors.newSingleThreadExecutor();
        brokerExec.execute(broker);

        new Thread(() -> {
            try {
                long brokerRunTime = 5000;
                System.out.println("Trying to block Broker's execution in " + brokerRunTime + " millisecs");
                brokerExec.awaitTermination(brokerRunTime, TimeUnit.MILLISECONDS);
                System.out.println("#...#");
            } catch (InterruptedException e) {
                System.out.println("Interrupted kill-switch Thread, lol");
            }
        }).start();
    }
}
