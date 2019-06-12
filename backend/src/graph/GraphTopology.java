package graph;

import nodes.*;
import pubsub.Broker;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GraphTopology {
    // Nodes
    Map<String, Source> sources;     // Maps SourceID -> Source
    Map<String, Sink> sinks;         // Maps SinkID -> Sink
    Map<String, Handler> handlers;   // Maps HandlerID -> Handler

    // Broker
    Broker<Object> broker;

    // Edges
    // Node1(output) -> Array<Node(input)>
    private Map<String, ArrayList<String>> edges;

    //NodeFactory
    private NodeFactory nodeFactory;

    GraphTopology() {
        this.sources = new HashMap<>();
        this.sinks = new HashMap<>();
        this.handlers = new HashMap<>();
        this.edges = new HashMap<>();
        this.broker = new Broker<>();
        this.nodeFactory = new NodeFactory();
    }

    public Source createSource(NodeFactory.SourceType sourceType) {
        Source source = nodeFactory.createSource(sourceType);
        String sourceKey = broker.register(source);
        sources.put(sourceKey, source);
        return source;
    }

    public Sink createSink(NodeFactory.SinkType sinkType) {
        Sink sink = nodeFactory.createSink(sinkType);
        String sinkKey = broker.register(sink);
        sinks.put(sinkKey, sink);
        return sink;
    }

    public Handler createHandler(NodeFactory.HandlerType handlerType) {
        Handler handler = nodeFactory.createHandler(handlerType);
        String handlerKey = broker.register(handler);
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

    public boolean createEdge(String sourceId, String sinkId) { //TODO: Check types
        Node source = getSourceNode(sourceId);
        Node sink = getSinkNode(sinkId);

        if (source == null || sink == null)
            return false;

        broker.addSubscriber(sinkId, sourceId);

        ArrayList<String> sinks = edges.getOrDefault(sourceId, new ArrayList<>());
        sinks.add(sinkId);
        edges.put(sourceId, sinks);
        return true;
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

    public boolean checkValidEdge(Object output, Object input) {
        Node outputNode = getSourceNode(output.toString());
        Node inputNode = getSinkNode(input.toString());

        int outputIndex = (outputNode instanceof Handler) ? 1 : 0;

        Class outputTypeClass = getGenericTypeNode(outputNode, outputIndex);
        Class inputTypeClass = getGenericTypeNode(inputNode, 0);

        return inputTypeClass.isAssignableFrom(outputTypeClass);
    }

    private Class getGenericTypeNode(Node node, int index) {
        Type mySuperclass = node.getClass().getGenericSuperclass();
        Type[] tType = ((ParameterizedType) mySuperclass).getActualTypeArguments();
        String typeName = tType[index].getTypeName();

        Class clazz = null;
        try {
            clazz = Class.forName(typeName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return clazz;
    }
}
