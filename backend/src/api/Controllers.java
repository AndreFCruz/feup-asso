package api;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.InfoSecCooker;

import java.io.*;
import java.net.URI;
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

    private static void sendResponse(HttpExchange he, Map<String, Object> parameters) throws IOException {
        String response = "";
        for (String key : parameters.keySet())
            response += key + " = " + parameters.get(key) + "\n";
        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();
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

    public static class EchoHeaderHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {
            Headers headers = he.getRequestHeaders();
            Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
            StringBuilder response = new StringBuilder();
            for (Map.Entry<String, List<String>> entry : entries)
                response.append(entry.toString()).append("\n");
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();
        }
    }

    public static class EchoGetHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {
            Map<String, Object> parameters = new HashMap<>();
            URI requestedUri = he.getRequestURI();
            String query = requestedUri.getRawQuery();
            parseQuery(query, parameters);

            sendResponse(he, parameters);
        }
    }

    public static class EchoPostHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {
            Map<String, Object> parameters = parseBody(he);

            sendResponse(he, parameters);
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
            String sourceKey = infoSecCooker.graph.createSource(convertSourceNameToSourceType(parameters.get("name").toString()));
            Map<String, Object> response = new HashMap<>();
            response.put("sourceKey", sourceKey);
            sendResponse(he, response);
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
            String sinkKey = infoSecCooker.graph.createSink(convertSinkNameToSinkType(parameters.get("name").toString()));
            Map<String, Object> response = new HashMap<>();
            response.put("sinkKey", sinkKey);
            sendResponse(he, response);
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
            String handlerKey = infoSecCooker.graph.createHandler(convertHandlerNameToHandlerType(parameters.get("name").toString()));
            Map<String, Object> response = new HashMap<>();
            response.put("handlerKey", handlerKey);
            sendResponse(he, response);
        }
    }

    public static class RemoveSource implements HttpHandler {
        private InfoSecCooker infoSecCooker;

        RemoveSource(InfoSecCooker infoSecCooker) {
            this.infoSecCooker = infoSecCooker;
        }

        @Override
        public void handle(HttpExchange he) throws IOException {
            Map<String, Object> parameters = parseBody(he);
            infoSecCooker.graph.removeSourceById(parameters.get("name").toString());
            sendResponse(he, new HashMap<>());
        }
    }

    public static class RemoveSink implements HttpHandler {
        private InfoSecCooker infoSecCooker;

        RemoveSink(InfoSecCooker infoSecCooker) {
            this.infoSecCooker = infoSecCooker;
        }

        @Override
        public void handle(HttpExchange he) throws IOException {
            Map<String, Object> parameters = parseBody(he);
            infoSecCooker.graph.removeSinkById(parameters.get("name").toString());
            sendResponse(he, new HashMap<>());
        }
    }

    public static class RemoveHandler implements HttpHandler {
        private InfoSecCooker infoSecCooker;

        RemoveHandler(InfoSecCooker infoSecCooker) {
            this.infoSecCooker = infoSecCooker;
        }

        @Override
        public void handle(HttpExchange he) throws IOException {
            Map<String, Object> parameters = parseBody(he);
            infoSecCooker.graph.removeHandlerById(parameters.get("name").toString());
            sendResponse(he, new HashMap<>());
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
            infoSecCooker.graph.createEdge(parameters.get("sourceId").toString(), parameters.get("sinkId").toString());
            sendResponse(he, new HashMap<>());
        }
    }

    public static class RemoveEdge implements HttpHandler {
        private InfoSecCooker infoSecCooker;

        RemoveEdge(InfoSecCooker infoSecCooker) {
            this.infoSecCooker = infoSecCooker;
        }

        @Override
        public void handle(HttpExchange he) throws IOException {
            Map<String, Object> parameters = parseBody(he);
            infoSecCooker.graph.removeEdge(parameters.get("sourceId").toString(), parameters.get("sinkId").toString());
            sendResponse(he, new HashMap<>());
        }
    }

    public static class RunGraph implements HttpHandler {
        private InfoSecCooker infoSecCooker;

        RunGraph(InfoSecCooker infoSecCooker) {
            this.infoSecCooker = infoSecCooker;
        }

        @Override
        public void handle(HttpExchange he) throws IOException {
            infoSecCooker.execute();
            sendResponse(he, new HashMap<>());
        }
    }

    public static class GetSources implements HttpHandler {
        private InfoSecCooker infoSecCooker;

        GetSources(InfoSecCooker infoSecCooker) {
            this.infoSecCooker = infoSecCooker;
        }

        @Override
        public void handle(HttpExchange he) throws IOException {
            Set<String> sourcesSet = infoSecCooker.graph.getSourcesIds();
            Map<String, Object> response = new HashMap<>();
            response.put("sources", sourcesSet);
            sendResponse(he, response);
        }
    }

    public static class GetSinks implements HttpHandler {
        private InfoSecCooker infoSecCooker;

        GetSinks(InfoSecCooker infoSecCooker) {
            this.infoSecCooker = infoSecCooker;
        }

        @Override
        public void handle(HttpExchange he) throws IOException {
            Set<String> sinksSet = infoSecCooker.graph.getSinksIds();
            Map<String, Object> response = new HashMap<>();
            response.put("sinks", sinksSet);
            sendResponse(he, response);
        }
    }

    public static class GetHandlers implements HttpHandler {
        private InfoSecCooker infoSecCooker;

        GetHandlers(InfoSecCooker infoSecCooker) {
            this.infoSecCooker = infoSecCooker;
        }


        @Override
        public void handle(HttpExchange he) throws IOException {
            Set<String> handlersSet = infoSecCooker.graph.getHandlersIds();
            Map<String, Object> response = new HashMap<>();
            response.put("handlers", handlersSet);
            sendResponse(he, response);
        }
    }
}
