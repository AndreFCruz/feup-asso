package manager;

import nodes.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Graph {
    // Nodes
    public HashMap<String, Source> sources;
    public HashMap<String, Sink> sinks;
    public HashMap<String, Handler> handlers;

    // Edges
    // Node1(input) -> Node2(output)
    private HashMap<String, String> edges;

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

    public String createSource(NodeFactory.SourceType sourceType) {
        Source source = nodeFactory.createSource(sourceType);
        String sourceKey = manager.register(source);
        sources.put(source.getName(), source);
        return sourceKey;
    }

    public String createSink(NodeFactory.SinkType sinkType) {
        Sink sink = nodeFactory.createSink(sinkType);
        String sinkKey = manager.register(sink);
        sinks.put(sink.getName(), sink);
        return sinkKey;
    }

    public String createHandler(NodeFactory.HandlerType handlerType) {
        Handler handler = nodeFactory.createHandler(handlerType);
        String handlerKeys = manager.register(handler);
        handlers.put(handler.getName(), handler);
        return handlerKeys;
    }

    public Set<String> getSourcesIds() {
        return sources.keySet();
    }

    public Set<String> getSinksIds() {
        return sinks.keySet();
    }

    public Set<String> getHandlersIds() {
        return handlers.keySet();
    }

    public void removeSourceById(String sourceId) {
        sources.remove(sourceId);
        edges.remove(sourceId);
    }

    public void removeSinkById(String sinkId) {
        sinks.remove(sinkId);
        for (String sourceKey : edges.keySet()) { //TODO: Correct this
            if (edges.get(sourceKey).equals(sinkId)) {
                edges.remove(sourceKey);
            }
        }
    }

    public void removeHandlerById(String handlerId) {
        handlers.remove(handlerId);
        ArrayList<String> nodesToRemove = new ArrayList<>();

        for (String sourceKey : edges.keySet()) { //TODO: Correct this
            if (sourceKey.equals(handlerId)) {
                nodesToRemove.add(sourceKey);
                continue;
            }
            if (edges.get(sourceKey).equals(handlerId)) {
                nodesToRemove.add(sourceKey);
            }
        }

        for (String key : nodesToRemove) {
            edges.remove(key);
        }
    }

    public boolean createEdge(String sourceId, String sinkId) {
        Node source = getSourceNode(sourceId);
        Node sink = getSinkNode(sinkId);

        if (source == null || sink == null)
            return false;

        manager.addSubscriber(sinkId, sourceId);
        edges.put(sourceId, sinkId);
        return true;
    }

    private Node getSourceNode(String sourceId) {
        Node source = sources.get(sourceId);

        if (source == null)
            for (String handlerKey : handlers.keySet()) {
                String[] handlerKeys = handlerKey.split("-");
                if (handlerKeys[1].equals(sourceId)) {
                    source = handlers.get(handlerKey);
                    break;
                }
            }

        return source;
    }

    private Node getSinkNode(String sinkId) {
        Node sink = sinks.get(sinkId);

        if (sink == null)
            for (String handlerKey : handlers.keySet()) {
                String[] handlerKeys = handlerKey.split("-");
                if (handlerKeys[0].equals(sinkId)) {
                    sink = handlers.get(handlerKey);
                    break;
                }
            }

        return sink;
    }
}