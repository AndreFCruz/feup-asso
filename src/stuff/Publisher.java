package stuff;

public class Publisher extends AbstractEntity implements Runnable {
    public double MAX_NUMBER_GENERATED = Math.exp(6);


    public Object generateMessage() throws InterruptedException {
        Thread.sleep(1000);
        return Math.floor(Math.random()*MAX_NUMBER_GENERATED);
    }

    private void publishMessage(Object message){
        queue.add(message);
    }

    @Override
    public void run() {
        double start = System.currentTimeMillis();
        while(start + runTime > System.currentTimeMillis()){
            Object message = null;
            try {
                message = generateMessage();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishMessage(message);
        }
    }
}
