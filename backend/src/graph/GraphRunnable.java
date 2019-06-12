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

    public GraphRunnable(GraphTopology graphTopology) {
        this.graphTopology = graphTopology;
    }

    public void start() {
        stop();
        brokerExec = Executors.newSingleThreadExecutor();
        executor = Executors.newCachedThreadPool();

        executeNodes();
        executeBroker();
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
        brokerExec.execute(graphTopology.manager);
    }

    public void stop() {
        long terminationTime = 1000;
        System.out.println("Trying to block Broker's execution in " + terminationTime + " millisecs");
        Utils.shutdownAndAwaitTermination(brokerExec, terminationTime);
        Utils.shutdownAndAwaitTermination(executor, terminationTime);
        System.out.println("#...#");
    }

    public GraphTopology getGraphTopology() {
        return graphTopology;
    }

}
