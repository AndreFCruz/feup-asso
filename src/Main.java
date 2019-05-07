import stuff.*;

public class Main {

    public static void main(String[] args) {
        // Create Broker
        Broker broker = new Broker(5000);

        // Create Publishers and populate registry
        Publisher pubA = new Publisher();
        int keyA = broker.addPublisher(pubA, true);

        Publisher pubB = new Publisher();
        int keyB = broker.addPublisher(pubB, false);

        // Create Subscribers
        Subscriber subsA = new Subscriber(1);
        Subscriber subsB = new Subscriber(2);
        Subscriber subsC = new Subscriber(3);
        Subscriber subsD = new Subscriber(4);

        // // Manage subscriptions
        broker.addSubscriber(subsA, keyA, true);
        broker.addSubscriber(subsA, keyB, true);
        broker.addSubscriber(subsB, keyB,true);
        broker.addSubscriber(subsC, keyA,false);

        new Thread(broker).start();
    }
}
