package manager;

import api.RESTServer;
import nodes.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

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

    public boolean loadGraph(JSONObject graphObj) {
        Map<String, Function<Object, Node>> typeNodeToCreateNode = new HashMap<>() {{
            put("sourceNode", (sourceType) -> graph.createSource((NodeFactory.SourceType) sourceType));
            put("handlerNode", (handlerType) -> graph.createHandler((NodeFactory.HandlerType) handlerType));
            put("sinkNode", (sinkType) -> graph.createSink((NodeFactory.SinkType) sinkType));
        }};

        Map<String, Node> frontendIdToRealId = new HashMap<>();
        JSONArray nodes = graphObj.getJSONArray("nodes");
        JSONArray edges = graphObj.getJSONArray("edges");

        for (int i = 0; i < nodes.length(); i++) {
            JSONObject nodeObj = nodes.getJSONObject(i);
            String nodeId = nodeObj.get("id").toString();
            String nodeType = nodeObj.get("type").toString();
            String nodeSubType = nodeObj.get("title").toString();
            Node node;
            switch (nodeType) {
                case "sourceNode":
                    NodeFactory.SourceType sourceType = NodeFactory.convertSourceNameToSourceType(nodeSubType);
                    node = graph.createSource(sourceType);
                    break;
                case "handlerNode":
                    NodeFactory.HandlerType handlerType = NodeFactory.convertHandlerNameToHandlerType(nodeSubType);
                    node = graph.createHandler(handlerType);
                    break;
                case "sinkNode":
                    NodeFactory.SinkType sinkType = NodeFactory.convertSinkNameToSinkType(nodeSubType);
                    node = graph.createSink(sinkType);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + nodeType);
            }
            frontendIdToRealId.put(nodeId, node);
        }

        for (int i = 0; i < edges.length(); i++) {
            JSONObject edge = edges.getJSONObject(i);
            String sourceId = edge.get("source").toString();
            String targetId = edge.get("target").toString();
            Node source = frontendIdToRealId.get(sourceId);
            Node target = frontendIdToRealId.get(targetId);
            if (source instanceof Handler)
                sourceId = ((Handler) source).getSourceId();
            else
                sourceId = (String) source.getId();

            if (target instanceof Handler)
                targetId = ((Handler) target).getSinkId();
            else
                targetId = (String) target.getId();

            graph.createEdge(sourceId, targetId);
        }


        return true;
    }

    public void initializeGraph() {
        // Create Publishers and populate registry
        Source stringSource = graph.createSource(NodeFactory.SourceType.STRING_GENERATOR);
        Source integerSource = graph.createSource(NodeFactory.SourceType.INTEGER_GENERATOR);

        // Create Handlers
        Handler md5Converter = graph.createHandler(NodeFactory.HandlerType.MD5_CONVERTER);
        Handler uppercase = graph.createHandler(NodeFactory.HandlerType.UPPER_CASE_CONVERTER);

        // Create Sinks
        Sink printerSink = graph.createSink(NodeFactory.SinkType.PRINTER);
        Sink fileWriterSink = graph.createSink(NodeFactory.SinkType.FILE_WRITER);


        // Manage subscriptions
        graph.createEdge((String) stringSource.getId(), (String) printerSink.getId());
        graph.createEdge((String) integerSource.getId(), (String) printerSink.getId());
        graph.createEdge((String) stringSource.getId(), uppercase.getSinkId());
        graph.createEdge(uppercase.getSourceId(), (String) fileWriterSink.getId());
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
