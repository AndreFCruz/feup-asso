package stuff;

public class Subscriber<T> extends AbstractEntity<T> {

    public Subscriber() { }

    void logMessage(Object message){
        System.out.println("Subscriber id " + id + " | Received the message: " + message);
    }

    private T pullMessage() throws InterruptedException {
        T message = queue.take();
        logMessage(message);
        return message;
    }

    @Override
    public void run() {
        double start = System.currentTimeMillis(); // TODO tirar tempo manhoso
        while (start + runTime > System.currentTimeMillis()) {
            try {
                pullMessage();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
