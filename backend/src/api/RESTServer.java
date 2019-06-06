package api;

import com.sun.net.httpserver.HttpServer;
import manager.InfoSecCooker;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RESTServer {
    private int PORT = 3000;
    private HttpServer server;
    private InfoSecCooker infoSecCooker;

    public RESTServer(InfoSecCooker infoSecCooker) throws IOException {
        this.infoSecCooker = infoSecCooker;
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        initializeRoutes();
    }

    public void start() {
        server.setExecutor(null);
        server.start();
        System.out.println("server started at " + PORT);
    }

    private void initializeRoutes() {
        server.createContext("/", new Controllers.RootHandler());
        server.createContext("/echoHeader", new Controllers.EchoHeaderHandler());
        server.createContext("/echoGet", new Controllers.EchoGetHandler());
        server.createContext("/echoPost", new Controllers.EchoPostHandler());

        server.createContext("/createSource", new Controllers.CreateSource(infoSecCooker));
        server.createContext("/createSink", new Controllers.CreateSink(infoSecCooker));
        server.createContext("/createHandler", new Controllers.CreateHandler(infoSecCooker));

        server.createContext("/removeSource", new Controllers.RemoveSource(infoSecCooker));
        server.createContext("/removeSink", new Controllers.RemoveSink(infoSecCooker));
        server.createContext("/removeHandler", new Controllers.RemoveHandler(infoSecCooker));

        server.createContext("/createEdge", new Controllers.CreateEdge(infoSecCooker));
        server.createContext("/removeEdge", new Controllers.RemoveEdge(infoSecCooker));
        server.createContext("/runGraph", new Controllers.RunGraph(infoSecCooker));

        server.createContext("/getSources", new Controllers.GetSources(infoSecCooker));
        server.createContext("/getSinks", new Controllers.GetSinks(infoSecCooker));
        server.createContext("/getHandlers", new Controllers.GetHandlers(infoSecCooker));
    }
}
