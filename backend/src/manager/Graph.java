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
    // Node1(input) -> Array<Node(output)>
    private Map<String, ArrayList<String>> edges;

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

    public Source createSource(NodeFactory.SourceType sourceType) {
        Source source = nodeFactory.createSource(sourceType);
        String sourceKey = manager.register(source);
        sources.put(sourceKey, source);
        return source;
    }

    public Sink createSink(NodeFactory.SinkType sinkType) {
        Sink sink = nodeFactory.createSink(sinkType);
        String sinkKey = manager.register(sink);
        sinks.put(sinkKey, sink);
        return sink;
    }

    public Handler createHandler(NodeFactory.HandlerType handlerType) {
        Handler handler = nodeFactory.createHandler(handlerType);
        String handlerKey = manager.register(handler);
        handlers.put(handlerKey, handler);
        return handler;
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

    public Map<String, ArrayList<String>> getEdges() {
        return edges;
    }

    public void removeSourceById(String sourceId) {
        sources.remove(sourceId);
        edges.remove(sourceId);
    }

    public void removeSinkById(String sinkId) {
        sinks.remove(sinkId);
        for (String sourceKey : edges.keySet()) {
            edges.get(sourceKey).remove(sinkId);
        }
    }

    public void removeHandlerById(String handlerId) {
        Handler handlerRemoved = handlers.remove(handlerId);

        edges.remove(handlerRemoved.getSourceId());
        for (String sourceKey : edges.keySet()) {
            edges.get(sourceKey).remove(handlerRemoved.getSinkId());
        }
    }

    public boolean createEdge(String sourceId, String sinkId) { //TODO: Check types
        Node source = getSourceNode(sourceId);
        Node sink = getSinkNode(sinkId);

        if (source == null || sink == null)
            return false;

        manager.addSubscriber(sinkId, sourceId);

        ArrayList<String> sinks = edges.getOrDefault(sourceId, new ArrayList<>());
        sinks.add(sinkId);
        edges.put(sourceId, sinks);
        return true;
    }

    public void removeEdge(String sourceId, String sinkId) {
        edges.get(sourceId).remove(sinkId);
    }

    private Node getSourceNode(String sourceId) {
        Node source = sources.get(sourceId);

        if (source == null)
            for (String handlerKey : handlers.keySet()) {
                Handler handler = handlers.get(handlerKey);
                if (handler.getSourceId().equals(sourceId)) {
                    return handler.getSource();
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
                    return handler.getSink();
                }
            }

        return sink;
    }

    public boolean checkValidEdge(Object source, Object sink) {
        System.out.println(source.toString());
        System.out.println(sink.toString());
        return true;
    }
}
