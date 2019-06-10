package nodes;

import manager.Broker;
import pubsub.Publisher;
import pubsub.Subscriber;

import java.util.concurrent.BlockingQueue;

public abstract class Handler<In, Out> extends Node<String> implements Subscriber<In, Out>, Publisher<Out>, Runnable {


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

    public String initialize(String sinkId, BlockingQueue<In> sinkQueue, String sourceId, BlockingQueue<Out> sourceQueue, Broker<Out> broker) {
        String sourceName = initializeSource(sourceId, sourceQueue, broker);
        String sinkName = initializeSink(sinkId, sinkQueue);
        return super.initialize(sinkName + '-' + sourceName);
    }

    private String initializeSource(String id, BlockingQueue<Out> queue, Broker<Out> broker) {
        return source.initialize(id, queue, broker);
    }

    private String initializeSink(String id, BlockingQueue<In> queue) {
        return sink.initialize(id, queue);
    }

    public String getSourceId() {
        return this.source.getId();
    }

    public String getSinkId() {
        return this.sink.getId();
    }

    public Node getSource() {
        return this.source;
    }

    public Node getSink() {
        return this.sink;
    }

    @Override
    public Out produceMessage() { //TODO: Refactor this
        try {
            In message = sink.pullMessage();
            return this.handleMessage(message);
        } catch (Exception e) {
            System.out.println(e);
            return (Out) "";
        }
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
