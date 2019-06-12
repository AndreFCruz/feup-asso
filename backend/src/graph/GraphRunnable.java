package graph;

import nodes.Handler;
import nodes.Sink;
import nodes.Source;
import utils.Utils;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GraphRunnable {
    private ExecutorService brokerExec = Executors.newSingleThreadExecutor();
    private ExecutorService executor = Executors.newCachedThreadPool();

    private GraphTopology graphTopology;
    private boolean isRunning;

    public GraphRunnable(GraphTopology graphTopology) {
        this.graphTopology = graphTopology;
        this.isRunning = false;
    }

    public void start() {
        stop();
        brokerExec = Executors.newSingleThreadExecutor();
        executor = Executors.newCachedThreadPool();

        executeNodes();
        executeBroker();
        isRunning = true;
    }

    private void executeNodes() {
        Collection<Source> sources = graphTopology.sources.values();
        Collection<Sink> sinks = graphTopology.sinks.values();
        Collection<Handler> handlers = graphTopology.handlers.values();

        sources.forEach(executor::submit);
        handlers.forEach(executor::submit);
        sinks.forEach(executor::submit);
    }

    private void executeBroker() {
        brokerExec.execute(graphTopology.broker);
    }

    public void stop() {
        if (!isRunning)
            return;

        long terminationTime = 1000;
        Utils.shutdownAndAwaitTermination(brokerExec, terminationTime);
        Utils.shutdownAndAwaitTermination(executor, terminationTime);
        System.out.println("#...#");
        isRunning = false;
    }

    public GraphTopology getGraphTopology() {
        return graphTopology;
    }

}
