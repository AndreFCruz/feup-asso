package manager;

import nodes.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Graph {
    // Nodes
    private HashMap<String, Source> sources;
    private HashMap<String, Sink> sinks;
    private HashMap<String, Handler> handlers;

    // Edges
    // Node1(input) -> Node2(output)
    private HashMap<String, String> edges = new HashMap<>();

    // Broker
    private Broker manager;

    //NodeFactory
    private NodeFactory nodeFactory;

    public Graph(Broker manager) {
        this.sources = new HashMap<>();
        this.sinks = new HashMap<>();
        this.handlers = new HashMap<>();
        this.edges = new HashMap<>();
        this.manager = manager;
        this.nodeFactory = new NodeFactory();
    }

    public String createSource(SourceType sourceType) {
        Source source = nodeFactory.createSource(sourceType);
        String sourceKey = manager.register(source);
        sources.put(source.getName(), source);
        return sourceKey;
    }

    public String createSink(SinkType sinkType) {
        Sink sink = nodeFactory.createSink(sinkType);
        String sinkKey = manager.register(sink);
        sinks.put(sink.getName(), sink);
        return sinkKey;
    }

    public String createHandler(HandlerType handlerType) {
        Handler handler = nodeFactory.createHandler(handlerType);
        String handlerKeys = manager.register(handler);
        handlers.put(handler.getName(), handler);
        return handlerKeys;
    }

    public Set<String> getSourcesIds(){
        return sources.keySet();
    }
    public Set<String> getSinksIds(){
        return sinks.keySet();
    }
    public Set<String> getHandlersIds(){
        return handlers.keySet();
    }
    public void removeSourceById(String sourceId) {
    }
    public void removeSinkById(String sinkId) {
    }
    public void removeHandlerById(String handlerId) {

    }
    //Remove Source by name
    //Remove Sink by name
    //Remove Handler by name
    //Create edge
    public boolean createEdge(String inputId, String outputId) {
        Node input = null;
        Node output = null;
        for (Node node : sources) {
            if (node.getName().equals(inputId)) {
                input = node;
                break;
            }
        }

        for (Node node: sinks) {
            if (node.getName().equals(outputId)) {
                output = node;
                break;
            }
        }

        if(input == null || output == null) {
            for (Node node: handlers) {
                String[] nodeKeys = node.getName().split("-");
                if(input == null) {
                    if (node.getName().equals(inputId)) {
                        output = node;
                        break;
                    }
                }
                if(output == null) {
                    if (node.getName().equals(outputId)) {
                        output = node;
                        break;
                    }
                }

            }
        }

        if(input == null || output == null)
            return false;

        manager.addSubscriber(output.getName(), input.getName());
    }

    // // Manage subscriptions
        manager.addSubscriber(printerSinkKey, stringSourceKey);
        manager.addSubscriber(printerSinkKey, integerSourceKey);
        manager.addSubscriber(uppercaseKeys[0], stringSourceKey);
        manager.addSubscriber(fileWriterSinkKey, uppercaseKeys[1]);

        executor.submit(stringSource);
        executor.submit(integerSource);

        executor.submit(uppercase);

        executor.submit(printerSink);
        executor.submit(fileWriterSink);

    ExecutorService brokerExec = Executors.newSingleThreadExecutor();
        brokerExec.execute(manager);

        new Thread(() -> {
        try {
            long brokerRunTime = 5000;
            System.out.println("Trying to block Broker's execution in " + brokerRunTime + " millisecs");
            brokerExec.awaitTermination(brokerRunTime, TimeUnit.MILLISECONDS);
            executor.shutdownNow();
            System.out.println("#...#");
        } catch (InterruptedException e) {
            System.out.println("Interrupted kill-switch Thread, lol");
        }
    }).start();
}

}
