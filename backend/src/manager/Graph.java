package manager;

import nodes.*;
import nodes.implementations.handlers.MD5Converter;
import nodes.implementations.handlers.Uppercase;
import nodes.implementations.sinks.FileWriter;
import nodes.implementations.sinks.Printer;
import nodes.implementations.sources.IntegerGenerator;
import nodes.implementations.sources.StringGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Graph {
    // Nodes
    private ArrayList<Source> sources;
    private ArrayList<Sink> sinks;
    private ArrayList<Handler> handlers;

    // Edges
    // publisherKey(Source | InputHandler) -> arraySubscriberKeys (Sink | OutputHandler)
    private Map<Integer, ArrayList<Integer>> observers = new HashMap<>();

    // Broker
    private Broker manager;

    //NodeFactory
    private NodeFactory nodeFactory;

    public Graph(Broker manager) {
        this.sources = new ArrayList<>();
        this.sinks = new ArrayList<>();
        this.handlers = new ArrayList<>();
        this.manager = manager;
        this.nodeFactory = new NodeFactory();
    }

    public int createSource(SourceType sourceType) {
        Source source = nodeFactory.createSource(sourceType);
        int sourceKey = manager.register(source);
        sources.add(source);
        return sourceKey;
    }

    public int createSink(SinkType sinkType) {
        Sink sink = nodeFactory.createSink(sinkType);
        int sinkKey = manager.register(sink);
        sinks.add(sink);
        return sinkKey;
    }

    public int[] createHandler(HandlerType handlerType) {
        Handler handler = nodeFactory.createHandler(handlerType);
        int[] handlerKeys = manager.register(handler);
        handlers.add(handler);
        return handlerKeys;
    }

    // Create Publishers and populate registry
    Source<String> stringSource = new StringGenerator();
    int stringSourceKey = manager.register(stringSource);

    Source<Integer> integerSource = new IntegerGenerator();
    int integerSourceKey = manager.register(integerSource);

    // Create Handlers
    Handler<Object, String> md5Converter = new MD5Converter();
    int[] md5ConverterKeys = manager.register(md5Converter);

    Handler<String, String> uppercase = new Uppercase();
    int[] uppercaseKeys = manager.register(uppercase);

    // Create Sinks
    Sink<Object, Void> printerSink = new Printer();
    int printerSinkKey = manager.register(printerSink);

    Sink<Object, Void> fileWriterSink = new FileWriter();
    int fileWriterSinkKey = manager.register(fileWriterSink);


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
