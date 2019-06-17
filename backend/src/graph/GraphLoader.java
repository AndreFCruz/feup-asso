package graph;

import nodes.*;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class GraphLoader {
    public static GraphTopology loadGraph() {
        GraphTopology graphTopology = new GraphTopology();

        // Create Publishers and populate registry
        Source stringSource = graphTopology.createSource(NodeFactory.SourceType.STRING_GENERATOR);
        Source integerSource = graphTopology.createSource(NodeFactory.SourceType.INTEGER_GENERATOR);
        Source readFileSource = graphTopology.createSource(NodeFactory.SourceType.FILE_READER);

        readFileSource.initializeSettings(new HashMap<>() {{
            put("path", "./Files/todo.txt");
        }});

        // Create Handlers
        Handler md5Converter = graphTopology.createHandler(NodeFactory.HandlerType.MD5_HASH);
        Handler uppercase = graphTopology.createHandler(NodeFactory.HandlerType.TO_UPPERCASE);

        // Create Sinks
        Sink printerSink = graphTopology.createSink(NodeFactory.SinkType.PRINTER);
        Sink fileWriterSink = graphTopology.createSink(NodeFactory.SinkType.FILE_WRITER);

        fileWriterSink.initializeSettings(new HashMap<>() {{
            put("path", "./Files/" + fileWriterSink.hashCode() + ".txt");
        }});

        // Manage subscriptions
        graphTopology.createEdge(stringSource, printerSink);
        graphTopology.createEdge(integerSource, printerSink);
        graphTopology.createEdge(stringSource, uppercase);
        graphTopology.createEdge(uppercase, fileWriterSink);
        graphTopology.createEdge(stringSource, md5Converter);
        graphTopology.createEdge(md5Converter, fileWriterSink);
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

        loadEdges(graphTopology, edges, nodesNameToNodeObject);

        if (graphTopology.hasErrors)
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
        String nodeSubType = nodeObj.get("subtype").toString();

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

        JSONObject settingsObj = (nodeObj.has("settings") ? nodeObj.getJSONObject("settings") : null);
        boolean success = node.initializeSettings(parseSettings(settingsObj));

        if (!success)
            graphTopology.hasErrors = true;

        return node;
    }

    private static Map<String, Object> parseSettings(JSONObject settings) {
        if (settings == null)
            return new HashMap<>();

        return Utils.JSONObjectToMap(settings);
    }

    private static void loadEdges(GraphTopology graphTopology, JSONArray edges, Map<String, Node> nodesNameToNodeObject) {
        for (int i = 0; i < edges.length(); i++) {
            JSONObject edge = edges.getJSONObject(i);
            String sourceId = edge.get("source").toString();
            String targetId = edge.get("target").toString();

            Node source = nodesNameToNodeObject.get(sourceId);
            Node target = nodesNameToNodeObject.get(targetId);

            if (!graphTopology.createEdge(source, target))
                graphTopology.hasErrors = true;
        }
    }
}
