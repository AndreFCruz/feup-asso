import stuff.*;

public class Main {

    public static void main(String[] args) {
        // Create Broker
        Broker<Integer> broker = new Broker<Integer>(5000);

        // Create Publishers and populate registry
        Publisher<Integer> pubA = new IntPublisher();
        int keyA = broker.addPublisher(pubA, true);

        Publisher<Integer> pubB = new IntPublisher();
        int keyB = broker.addPublisher(pubB, false);

        // Create Subscribers
        Subscriber<Integer> subsA = new Subscriber<Integer>(1);
        Subscriber<Integer> subsB = new Subscriber<Integer>(2);
        Subscriber<Integer> subsC = new Subscriber<Integer>(3);
        Subscriber<Integer> subsD = new Subscriber<Integer>(4);

        // // Manage subscriptions
        broker.addSubscriber(subsA, keyA, true);
        broker.addSubscriber(subsA, keyB, true);
        broker.addSubscriber(subsB, keyB,true);
        broker.addSubscriber(subsC, keyA,false);

        new Thread(broker).start();
    }
}
