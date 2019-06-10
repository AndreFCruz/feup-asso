package manager;

import api.RESTServer;
import nodes.*;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Utils;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    public boolean loadGraph(JSONObject graphObj) {
        resetGraph();
        JSONArray nodes = graphObj.getJSONArray("nodes");
        JSONArray edges = graphObj.getJSONArray("edges");

        Map<String, Node> nodesNameToNodeObject = loadNodes(nodes);

        loadEdges(edges, nodesNameToNodeObject);

        return true;
    }

    private void resetGraph() {
        this.manager = new Broker<>();
        this.graph = new Graph(manager);
    }

    private Map<String, Node> loadNodes(JSONArray nodes) {
        Map<String, Node> nodesNameToNodeObject = new HashMap<>();

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

            nodesNameToNodeObject.put(nodeId, node);
        }

        return nodesNameToNodeObject;
    }

    private void loadEdges(JSONArray edges, Map<String, Node> nodesNameToNodeObject) {
        for (int i = 0; i < edges.length(); i++) {
            JSONObject edge = edges.getJSONObject(i);
            String sourceId = edge.get("source").toString();
            String targetId = edge.get("target").toString();

            Node source = nodesNameToNodeObject.get(sourceId);
            Node target = nodesNameToNodeObject.get(targetId);

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
        Utils.shutdownAndAwaitTermination(brokerExec, terminationTime);
        Utils.shutdownAndAwaitTermination(executor, terminationTime);
        System.out.println("#...#");
    }
}
