import manager.Broker;
import manager.Graph;
import nodes.Handler;
import nodes.NodeFactory;
import nodes.Sink;
import nodes.Source;
import nodes.implementations.handlers.MD5Converter;
import nodes.implementations.handlers.Uppercase;
import nodes.implementations.sinks.FileWriter;
import nodes.implementations.sinks.Printer;
import nodes.implementations.sources.IntegerGenerator;
import nodes.implementations.sources.StringGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        final ExecutorService executor = Executors.newCachedThreadPool();

        //Initialize broker
        Broker<Object> manager = new Broker<>();
        //Initialize Graph
        Graph graph = new Graph(manager);

        //Initialize server

        // execute nodes
        // stop nodes

        // Create Publishers and populate registry
        String stringSourceKey = graph.createSource(NodeFactory.SourceType.STRINGGENERATOR);
        String integerSourceKey = graph.createSource(NodeFactory.SourceType.INTEGERGENERATOR);

        // Create Handlers
//        String[] md5ConverterKeys = graph.createHandler(NodeFactory.HandlerType.MD5CONVERTER).split("-");
        String[] uppercaseKeys = graph.createHandler(NodeFactory.HandlerType.UPPERCASE).split("-");

        // Create Sinks
        String printerSinkKey = graph.createSink(NodeFactory.SinkType.PRINTER);
        String fileWriterSinkKey = graph.createSink(NodeFactory.SinkType.FILEWRITER);


        // Manage subscriptions
        graph.createEdge(stringSourceKey, printerSinkKey);
        graph.createEdge(integerSourceKey, printerSinkKey);
        graph.createEdge(stringSourceKey, uppercaseKeys[0]);
        graph.createEdge(uppercaseKeys[1], fileWriterSinkKey);

        Collection<Source> sources = graph.sources.values();
        Collection<Sink> sinks = graph.sinks.values();
        Collection<Handler> handlers = graph.handlers.values();

        sources.forEach(executor::submit);
        handlers.forEach(executor::submit);
        sinks.forEach(executor::submit);

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
