package stuff.implementations;


import stuff.AbstractEntity;
import stuff.Subscriber;

public class ConcreteSubscriber extends AbstractEntity<Integer> implements Subscriber<Integer> {

    // May block when handling message
    public void handleMessage(Integer message) {
        System.out.println("Subscriber id " + this.getId() + " | Received the message: " + message);
    }

}
