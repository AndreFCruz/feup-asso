package nodes;

import pubsub.Broker;
import pubsub.Publisher;
import pubsub.Subscriber;
import pubsub.helpers.EntityQueue;
import utils.Log;

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

    public String initialize(EntityQueue<In> entityQueueSubscribe, EntityQueue<Out> entityQueuePublish, Broker<Out> broker) {
        String sinkName = sink.initialize(entityQueueSubscribe);
        String sourceName = source.initialize(entityQueuePublish, broker);
        return super.initialize(sinkName + '-' + sourceName);
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
                Out msg = this.produceMessage();
                if (msg != null)
                    source.publishMessage(msg);
            }
        } catch (InterruptedException e) {
            Log.log("[Handler " + this.sink.getId() + "|" + this.source.getId() + "] Thread interrupted");
        }
    }
}
