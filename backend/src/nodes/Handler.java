package nodes;

import manager.Broker;
import pubsub.Publisher;
import pubsub.Subscriber;

import java.util.concurrent.BlockingQueue;

public abstract class Handler<In, Out> extends Node implements Subscriber<In, Out>, Publisher<Out>, Runnable {


    private Source<Out> source;
    private Sink<In, Out> sink;

    public Handler() {
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

    public String initialize(int sourceId, BlockingQueue<Out> sourceQueue, Broker<Out> broker, int sinkId, BlockingQueue<In> sinkQueue) {
        String sourceName = initializeSource(sourceId, sourceQueue, broker);
        String sinkName = initializeSink(sinkId, sinkQueue);
        return super.initialize(sinkName + '-' + sourceName);

    }

    private String initializeSource(int id, BlockingQueue<Out> queue, Broker<Out> broker) {
        return source.initialize(id, queue, broker);
    }

    private String initializeSink(int id, BlockingQueue<In> queue) {
        return sink.initialize(id, queue);
    }

    @Override
    public Out produceMessage() throws InterruptedException {
        In message = sink.pullMessage();
        return this.handleMessage(message);
    }

    // This method can cease to exist, and instead launch threads for this handler's sink/source references;
    // Sink will pull message, handle message, and pass to source, which publishes it and notifies the Broker.
    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                source.publishMessage(this.produceMessage());
            }
        } catch (InterruptedException e) {
            System.out.println("Handler " + this.sink.getId() + "|" + this.source.getId() + " Thread interrupted");
        }
    }
}
