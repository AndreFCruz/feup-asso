package api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.InfoSecCooker;
import nodes.Handler;
import nodes.Node;
import nodes.NodeFactory;
import nodes.Sink;
import org.json.JSONObject;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static nodes.NodeFactory.*;

class Controllers {
    private static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {
        if (query != null) {
            String[] pairs = query.split("[&]");
            for (String pair : pairs) {
                String[] param = pair.split("[=]");
                String key = null;
                String value = null;
                if (param.length > 0) {
                    key = URLDecoder.decode(param[0],
                            System.getProperty("file.encoding"));
                }

                if (param.length > 1) {
                    value = URLDecoder.decode(param[1],
                            System.getProperty("file.encoding"));
                }

                if (parameters.containsKey(key)) {
                    Object obj = parameters.get(key);
                    if (obj instanceof List<?>) {
                        List<String> values = (List<String>) obj;
                        values.add(value);

                    } else if (obj instanceof String) {
                        List<String> values = new ArrayList<>();
                        values.add((String) obj);
                        values.add(value);
                        parameters.put(key, values);
                    }
                } else {
                    parameters.put(key, value);
                }
            }
        }
    }

    private static Map<String, Object> parseBody(HttpExchange he) throws IOException {
        Map<String, Object> parameters = new HashMap<>();
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        parseQuery(query, parameters);
        return parameters;
    }

    private static JSONObject parseBodyToJSONObj(HttpExchange he) throws IOException {
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = br.readLine()) != null)
            response.append(line);

        return new JSONObject(response.toString());
    }

    private static void sendJSONResponse(HttpExchange he, Object responseObject) throws IOException {
        Gson gson = new Gson();
        String response = gson.toJson(responseObject);
        he.getResponseHeaders().set("Content-Type", "application/json");

        // Allow CORS
        he.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

        if (he.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            he.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
            he.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
            he.sendResponseHeaders(204, -1);
            return;
        }

        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public static class GetNodeTypes implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {
            if (!he.getRequestMethod().equalsIgnoreCase("GET"))
                return;

            Map<String, Object> response = new HashMap<>();
            response.put("sources", NodeFactory.getSourceNames());
            response.put("handlers", NodeFactory.getHandlerNames());
            response.put("sinks", NodeFactory.getSinkNames());
            sendJSONResponse(he, response);
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

    public static class CreateSource implements HttpHandler {
        private InfoSecCooker infoSecCooker;

        CreateSource(InfoSecCooker infoSecCooker) {
            this.infoSecCooker = infoSecCooker;
        }

        @Override
        public void handle(HttpExchange he) throws IOException {
            Map<String, Object> parameters = parseBody(he);
            Node source = infoSecCooker.graph.getGraphTopology().createSource(convertSourceNameToSourceType(parameters.get("name").toString()));
            Map<String, Object> response = new HashMap<>();
            response.put("sourceKey", source.getId());
            sendJSONResponse(he, response);
        }
    }

    public static class CreateSink implements HttpHandler {
        private InfoSecCooker infoSecCooker;

        CreateSink(InfoSecCooker infoSecCooker) {
            this.infoSecCooker = infoSecCooker;
        }

        @Override
        public void handle(HttpExchange he) throws IOException {
            Map<String, Object> parameters = parseBody(he);
            Sink sink = infoSecCooker.graph.getGraphTopology().createSink(convertSinkNameToSinkType(parameters.get("name").toString()));
            Map<String, Object> response = new HashMap<>();
            response.put("sinkKey", sink.getId());
            sendJSONResponse(he, response);
        }
    }

    public static class CreateHandler implements HttpHandler {
        private InfoSecCooker infoSecCooker;

        CreateHandler(InfoSecCooker infoSecCooker) {
            this.infoSecCooker = infoSecCooker;
        }

        @Override
        public void handle(HttpExchange he) throws IOException {
            Map<String, Object> parameters = parseBody(he);
            Handler handler = infoSecCooker.graph.getGraphTopology().createHandler(convertHandlerNameToHandlerType(parameters.get("name").toString()));
            Map<String, Object> response = new HashMap<>();
            response.put("handlerKey", handler.getId());
            sendJSONResponse(he, response);
        }
    }

    public static class CreateEdge implements HttpHandler {
        private InfoSecCooker infoSecCooker;

        CreateEdge(InfoSecCooker infoSecCooker) {
            this.infoSecCooker = infoSecCooker;
        }

        @Override
        public void handle(HttpExchange he) throws IOException {
            Map<String, Object> parameters = parseBody(he);
            infoSecCooker.graph.getGraphTopology().createEdge(parameters.get("sourceId").toString(), parameters.get("sinkId").toString());
            sendJSONResponse(he, new HashMap<>());
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
        private InfoSecCooker infoSecCooker;

        CheckEdge(InfoSecCooker infoSecCooker) {
            this.infoSecCooker = infoSecCooker;
        }

        @Override
        public void handle(HttpExchange he) throws IOException {
            JSONObject body = parseBodyToJSONObj(he);
            boolean success = infoSecCooker.graph.getGraphTopology().checkValidEdge(body.get("source"), body.get("sink"));
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
