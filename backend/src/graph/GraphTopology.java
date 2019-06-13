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

    public GraphTopology() {
        this.sources = new HashMap<>();
        this.sinks = new HashMap<>();
        this.handlers = new HashMap<>();
        this.edges = new HashMap<>();
        this.broker = new Broker<>();
        this.nodeFactory = new NodeFactory();
    }

    Source createSource(NodeFactory.SourceType sourceType) {
        Source source = nodeFactory.createSource(sourceType);
        String sourceKey = broker.register(source);
        sources.put(sourceKey, source);
        return source;
    }

    Sink createSink(NodeFactory.SinkType sinkType) {
        Sink sink = nodeFactory.createSink(sinkType);
        String sinkKey = broker.register(sink);
        sinks.put(sinkKey, sink);
        return sink;
    }

    Handler createHandler(NodeFactory.HandlerType handlerType) {
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

    boolean createEdge(Node outputNode, Node inputNode) {
        if (!checkValidEdge(outputNode, inputNode))
            return false;

        String outputId = getSourceNodeId(outputNode);
        String inputId = getSinkNodeId(inputNode);

        broker.addSubscriber(inputId, outputId);

        ArrayList<String> sinks = edges.getOrDefault(outputId, new ArrayList<>());
        sinks.add(inputId);
        edges.put(outputId, sinks);
        return true;
    }

    private String getSourceNodeId(Node sourceNode) {
        return (sourceNode instanceof Handler) ? ((Handler) sourceNode).getSourceId() : (String) sourceNode.getId();
    }

    private String getSinkNodeId(Node sinkNode) {
        return (sinkNode instanceof Handler) ? ((Handler) sinkNode).getSinkId() : (String) sinkNode.getId();
    }

    public boolean checkValidEdge(Node outputNode, Node inputNode) {
        if (outputNode == null || inputNode == null)
            return false;

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
