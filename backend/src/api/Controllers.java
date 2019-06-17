package api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import graph.GraphLoader;
import graph.GraphTopology;
import manager.InfoSecCooker;
import nodes.Node;
import nodes.NodeFactory;
import nodes.Source;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static api.Utils.*;

class Controllers {
    public static class GetNodeTypes implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {
            if (!he.getRequestMethod().equalsIgnoreCase("GET"))
                return;
            JSONObject response = new JSONObject();

            NodeFactory.SourceType[] sources = NodeFactory.getSourceNames();
            JSONObject sourcesMap = new JSONObject();
            for (NodeFactory.SourceType source : sources) {
                JSONObject sourceObj = new JSONObject();

                Source sourceNode = NodeFactory.createSource(source);
                String[] settings = sourceNode.getSettingsKeys();

                sourceObj.put("settings", settings);
                sourceObj.put("outputType", GraphTopology.getGenericTypeNode(sourceNode, 0).getTypeName());
                sourcesMap.put(source.toString(), sourceObj);
            }

            NodeFactory.HandlerType[] handlers = NodeFactory.getHandlerNames();
            JSONObject handlersMap = new JSONObject();
            for (NodeFactory.HandlerType handler : handlers) {
                JSONObject handlerObj = new JSONObject();

                Node handlerNode = NodeFactory.createHandler(handler);
                String[] settings = handlerNode.getSettingsKeys();

                handlerObj.put("settings", settings);
                handlerObj.put("outputType", GraphTopology.getGenericTypeNode(handlerNode, 1).getTypeName());
                handlerObj.put("inputType", GraphTopology.getGenericTypeNode(handlerNode, 0).getTypeName());
                handlersMap.put(handler.toString(), handlerObj);
            }

            NodeFactory.SinkType[] sinks = NodeFactory.getSinkNames();
            JSONObject sinksMap = new JSONObject();
            for (NodeFactory.SinkType sink : sinks) {
                JSONObject sinkObj = new JSONObject();

                Node sinkNode = NodeFactory.createSink(sink);
                String[] settings = sinkNode.getSettingsKeys();

                sinkObj.put("settings", settings);
                sinkObj.put("inputType", GraphTopology.getGenericTypeNode(sinkNode, 0).getTypeName());
                sinksMap.put(sink.toString(), sinkObj);
            }

            response.put("sources", sourcesMap);
            response.put("handlers", handlersMap);
            response.put("sinks", sinksMap);
            sendJSONObjectResponse(he, response);
        }
    }

    public static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {
            String response = "<h1>InfoSecCooker start success if you see this message</h1> ";
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public static class RunGraph implements HttpHandler {
        private InfoSecCooker infoSecCooker;

        RunGraph(InfoSecCooker infoSecCooker) {
            this.infoSecCooker = infoSecCooker;
        }

        @Override
        public void handle(HttpExchange he) throws IOException {
            infoSecCooker.startGraph();
            sendJSONResponse(he, new HashMap<>());
        }
    }

    public static class StopGraph implements HttpHandler {
        private InfoSecCooker infoSecCooker;

        StopGraph(InfoSecCooker infoSecCooker) {
            this.infoSecCooker = infoSecCooker;
        }

        @Override
        public void handle(HttpExchange he) throws IOException {
            infoSecCooker.stopGraph();
            sendJSONResponse(he, new HashMap<>());
        }
    }

    public static class GetSources implements HttpHandler {
        private InfoSecCooker infoSecCooker;

        GetSources(InfoSecCooker infoSecCooker) {
            this.infoSecCooker = infoSecCooker;
        }

        @Override
        public void handle(HttpExchange he) throws IOException {
            Set<String> sourcesSet = infoSecCooker.graph.getGraphTopology().getSourcesIds();
            Map<String, Object> response = new HashMap<>();
            response.put("sources", sourcesSet);
            sendJSONResponse(he, response);
        }
    }

    public static class GetSinks implements HttpHandler {
        private InfoSecCooker infoSecCooker;

        GetSinks(InfoSecCooker infoSecCooker) {
            this.infoSecCooker = infoSecCooker;
        }

        @Override
        public void handle(HttpExchange he) throws IOException {
            Set<String> sinksSet = infoSecCooker.graph.getGraphTopology().getSinksIds();
            Map<String, Object> response = new HashMap<>();
            response.put("sinks", sinksSet);
            sendJSONResponse(he, response);
        }
    }

    public static class GetHandlers implements HttpHandler {
        private InfoSecCooker infoSecCooker;

        GetHandlers(InfoSecCooker infoSecCooker) {
            this.infoSecCooker = infoSecCooker;
        }


        @Override
        public void handle(HttpExchange he) throws IOException {
            Set<String> handlersSet = infoSecCooker.graph.getGraphTopology().getHandlersIds();
            Map<String, Object> response = new HashMap<>();
            response.put("handlers", handlersSet);
            sendJSONResponse(he, response);
        }
    }

    public static class GetEdges implements HttpHandler {
        private InfoSecCooker infoSecCooker;

        GetEdges(InfoSecCooker infoSecCooker) {
            this.infoSecCooker = infoSecCooker;
        }

        @Override
        public void handle(HttpExchange he) throws IOException {
            Map<String, Object> response = new HashMap<>();
            response.put("edges", infoSecCooker.graph.getGraphTopology().getEdges());
            sendJSONResponse(he, response);
        }
    }

    public static class CheckEdge implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {
            JSONObject body = parseBodyToJSONObj(he);
            JSONObject outputObj = body.getJSONObject("output");
            JSONObject inputObj = body.getJSONObject("input");

            GraphTopology graphTopology = new GraphTopology();
            Node outputNode = GraphLoader.createNode(graphTopology, outputObj);
            Node inputNode = GraphLoader.createNode(graphTopology, inputObj);

            boolean success = graphTopology.checkValidEdge(outputNode, inputNode);
            sendJSONResponse(he, success);
        }
    }


    public static class SendGraph implements HttpHandler {
        private InfoSecCooker infoSecCooker;

        SendGraph(InfoSecCooker infoSecCooker) {
            this.infoSecCooker = infoSecCooker;
        }

        @Override
        public void handle(HttpExchange he) throws IOException {
            boolean success = infoSecCooker.loadGraph(parseBodyToJSONObj(he));
            sendJSONResponse(he, success);
        }
    }
}
