package manager;

import api.RESTServer;
import nodes.Handler;
import nodes.NodeFactory;
import nodes.Sink;
import nodes.Source;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class InfoSecCooker implements Runnable {
    public RESTServer restServer;

    public Graph graph;
    private Broker<Object> manager;

    private ExecutorService brokerExec = Executors.newSingleThreadExecutor();
    private ExecutorService executor = Executors.newCachedThreadPool();

    public InfoSecCooker() throws IOException {
        this.manager = new Broker<>();
        this.graph = new Graph(manager);
        this.restServer = new RESTServer(this);
    }

    private static void shutdownAndAwaitTermination(ExecutorService pool, long time) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(time, TimeUnit.MILLISECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(time, TimeUnit.MILLISECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public void initializeGraph() {
        // Create Publishers and populate registry
        String stringSourceKey = graph.createSource(NodeFactory.SourceType.STRINGGENERATOR);
        String integerSourceKey = graph.createSource(NodeFactory.SourceType.INTEGERGENERATOR);

        // Create Handlers
        String[] md5ConverterKeys = graph.createHandler(NodeFactory.HandlerType.MD5CONVERTER).split("-");
        String[] uppercaseKeys = graph.createHandler(NodeFactory.HandlerType.UPPERCASE).split("-");

        // Create Sinks
        String printerSinkKey = graph.createSink(NodeFactory.SinkType.PRINTER);
        String fileWriterSinkKey = graph.createSink(NodeFactory.SinkType.FILEWRITER);


        // Manage subscriptions
        graph.createEdge(stringSourceKey, printerSinkKey);
        graph.createEdge(integerSourceKey, printerSinkKey);
        graph.createEdge(stringSourceKey, uppercaseKeys[0]);
        graph.createEdge(uppercaseKeys[1], fileWriterSinkKey);
    }

    private void execute() {
        stop();
        brokerExec = Executors.newSingleThreadExecutor();
        executor = Executors.newCachedThreadPool();

        executeNodes();
        executeBroker();
    }

    private void executeNodes() {
        Collection<Source> sources = graph.sources.values();
        Collection<Sink> sinks = graph.sinks.values();
        Collection<Handler> handlers = graph.handlers.values();

        sources.forEach(executor::submit);
        handlers.forEach(executor::submit);
        sinks.forEach(executor::submit);
    }

    private void executeBroker() {
        brokerExec.execute(manager);
    }

    @Override
    public void run() {
        execute();
    }

    public void stop() {
        long terminationTime = 1000;
        System.out.println("Trying to block Broker's execution in " + terminationTime + " millisecs");
        shutdownAndAwaitTermination(brokerExec, terminationTime);
        shutdownAndAwaitTermination(executor, terminationTime);
        System.out.println("#...#");
    }
}