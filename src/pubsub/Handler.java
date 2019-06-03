package pubsub;

import manager.Broker;

import java.util.concurrent.BlockingQueue;

public abstract class Handler<In, Out> implements Subscriber<In, Out>, Publisher<Out>, Runnable {


    private Source<Out> source;
    private Sink<In, Out> sink;

    Handler() {
        Handler<In, Out> thisHandler = this;
        this.source = new Source<>() {
            @Override
            public Out produceMessage() throws InterruptedException {
                return thisHandler.produceMessage();
            }
        };

        this.sink = new Sink<>() {
            @Override
            public Out handleMessage(In message) throws InterruptedException {
                return thisHandler.handleMessage(message);
            }
        };
    }

    public void initializeSource(int id, BlockingQueue<Out> queue, Broker<Out> broker) {
        source.initialize(id, queue, broker);
    }

    public void initializeSink(int id, BlockingQueue<In> queue) {
        sink.initialize(id, queue);
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                In message = sink.pullMessage();
                Out processedMessage = sink.handleMessage(message);
                source.publishMessage(processedMessage);
            }
        } catch (InterruptedException e) {
            System.out.println("Handler " + this.sink.getId() + "|" + this.source.getId() + " Thread interrupted");
        }
    }
}
