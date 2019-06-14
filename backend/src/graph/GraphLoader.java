package graph;

import nodes.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GraphLoader {
    public static GraphTopology loadGraph() {
        GraphTopology graphTopology = new GraphTopology();

        // Create Publishers and populate registry
        Source stringSource = graphTopology.createSource(NodeFactory.SourceType.STRING_GENERATOR);
        Source integerSource = graphTopology.createSource(NodeFactory.SourceType.INTEGER_GENERATOR);
        Source readFileSource = graphTopology.createSource(NodeFactory.SourceType.READ_FILE);
        Map<String, String> settings = new HashMap<>() {{
            put("path", "./todo.txt");
        }};

        readFileSource.initializeSettings(settings);

        // Create Handlers
        Handler md5Converter = graphTopology.createHandler(NodeFactory.HandlerType.MD5_CONVERTER);
        Handler uppercase = graphTopology.createHandler(NodeFactory.HandlerType.UPPER_CASE_CONVERTER);

        // Create Sinks
        Sink printerSink = graphTopology.createSink(NodeFactory.SinkType.PRINTER);
        Sink fileWriterSink = graphTopology.createSink(NodeFactory.SinkType.FILE_WRITER);


        // Manage subscriptions
        graphTopology.createEdge(stringSource, printerSink);
        graphTopology.createEdge(integerSource, printerSink);
        graphTopology.createEdge(stringSource, uppercase);
        graphTopology.createEdge(uppercase, fileWriterSink);
        graphTopology.createEdge(readFileSource, printerSink);
        return graphTopology;
    }

    public static GraphTopology loadGraph(JSONObject graphObj) {
        if (graphObj.isEmpty())
            return null;

        JSONArray nodes = graphObj.getJSONArray("nodes");
        JSONArray edges = graphObj.getJSONArray("edges");

        if (nodes == null || edges == null)
            return null;

        GraphTopology graphTopology = new GraphTopology();
        Map<String, Node> nodesNameToNodeObject = loadNodes(graphTopology, nodes);

        if (!loadEdges(graphTopology, edges, nodesNameToNodeObject))
            return null;

        return graphTopology;
    }

    private static Map<String, Node> loadNodes(GraphTopology graphTopology, JSONArray nodes) {
        Map<String, Node> nodesNameToNodeObject = new HashMap<>();

        for (int i = 0; i < nodes.length(); i++) {
            JSONObject nodeObj = nodes.getJSONObject(i);
            String nodeId = nodeObj.get("id").toString();

            Node node = createNode(graphTopology, nodeObj);

            nodesNameToNodeObject.put(nodeId, node);
        }

        return nodesNameToNodeObject;
    }

    public static Node createNode(GraphTopology graphTopology, JSONObject nodeObj) {
        String nodeType = nodeObj.get("type").toString();
        String nodeSubType = nodeObj.get("title").toString();

        Node node;
        switch (nodeType) {
            case "sourceNode":
                NodeFactory.SourceType sourceType = NodeFactory.convertSourceNameToSourceType(nodeSubType);
                node = graphTopology.createSource(sourceType);
                break;
            case "handlerNode":
                NodeFactory.HandlerType handlerType = NodeFactory.convertHandlerNameToHandlerType(nodeSubType);
                node = graphTopology.createHandler(handlerType);
                break;
            case "sinkNode":
                NodeFactory.SinkType sinkType = NodeFactory.convertSinkNameToSinkType(nodeSubType);
                node = graphTopology.createSink(sinkType);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + nodeType);
        }

        return node;
    }

    private static boolean loadEdges(GraphTopology graphTopology, JSONArray edges, Map<String, Node> nodesNameToNodeObject) {
        for (int i = 0; i < edges.length(); i++) {
            JSONObject edge = edges.getJSONObject(i);
            String sourceId = edge.get("source").toString();
            String targetId = edge.get("target").toString();

            Node source = nodesNameToNodeObject.get(sourceId);
            Node target = nodesNameToNodeObject.get(targetId);

            if (!graphTopology.createEdge(source, target))
                return false;
        }

        return true;
    }
}
