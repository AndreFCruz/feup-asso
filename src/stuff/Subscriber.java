package stuff;

public class Subscriber extends AbstractEntity implements Runnable {
    private int id;

    public Subscriber(int id){
        this.id = id;
    }

    void logMessage(Object message){
        System.out.println("Subscriber id " + id + " | Received the message: " + message);
    }

    private Object pullMessage() throws InterruptedException {
        Object message = queue.take();
        logMessage(message);
        return message;
    }

    @Override
    public void run() {
        double start = System.currentTimeMillis();
        while(start + runTime > System.currentTimeMillis()){
            try {
                pullMessage();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
