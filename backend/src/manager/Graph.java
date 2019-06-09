package manager;

import nodes.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Graph {
    // Nodes
    Map<String, Source> sources;     // Maps SourceID -> Source
    Map<String, Sink> sinks;         // Maps SinkID -> Sink
    Map<String, Handler> handlers;   // Maps HandlerID -> Handler

    // Edges
    // Node1(input) -> Node2(output)
    private Map<String, String> edges; // TODO: Change to String -> Array<String>

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
        sources.put((String) source.getId(), source);
        return sourceKey;
    }

    public String createSink(NodeFactory.SinkType sinkType) {
        Sink sink = nodeFactory.createSink(sinkType);
        String sinkKey = manager.register(sink);
        sinks.put((String) sink.getId(), sink);
        return sinkKey;
    }

    public String createHandler(NodeFactory.HandlerType handlerType) {
        Handler handler = nodeFactory.createHandler(handlerType);
        String handlerKeys = manager.register(handler);
        handlers.put((String) handler.getId(), handler);
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
        for (String sourceKey : edges.keySet()) {
            if (edges.get(sourceKey).equals(sinkId)) {
                edges.remove(sourceKey);
            }
        }
    }

    public void removeHandlerById(String handlerId) {
        Handler handlerRemoved = handlers.remove(handlerId);
        ArrayList<String> nodesToRemove = new ArrayList<>();

        for (String sourceKey : edges.keySet()) {
            if (sourceKey.equals(handlerRemoved.getSourceId())) {
                nodesToRemove.add(sourceKey);
                continue;
            }

            if (edges.get(sourceKey).equals(handlerRemoved.getSinkId())) {
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

    public void removeEdge(String sourceId, String sinkId) {
        edges.remove(sourceId);
    }

    private Node getSourceNode(String sourceId) {
        Node source = sources.get(sourceId);

        if (source == null)
            for (String handlerKey : handlers.keySet()) {
                Handler handler = handlers.get(handlerKey);
                if (handler.getSourceId().equals(sourceId)) {
                    return handler;
                }
            }

        return source;
    }

    private Node getSinkNode(String sinkId) {
        Node sink = sinks.get(sinkId);

        if (sink == null)
            for (String handlerKey : handlers.keySet()) {
                Handler handler = handlers.get(handlerKey);
                if (handler.getSinkId().equals(sinkId)) {
                    return handler;
                }
            }

        return sink;
    }
}
