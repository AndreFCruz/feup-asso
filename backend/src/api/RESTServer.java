package api;

import com.sun.net.httpserver.HttpServer;
import manager.InfoSecCooker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RESTServer {
    private int PORT = 3000;
    private HttpServer server;
    private InfoSecCooker infoSecCooker;
    private ExecutorService RESTServerExec = Executors.newSingleThreadExecutor();


    public RESTServer(InfoSecCooker infoSecCooker) throws IOException {
        this.infoSecCooker = infoSecCooker;
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        initializeRoutes();
    }

    public void start() {
        server.setExecutor(RESTServerExec);
        server.start();
        System.out.println("Server started at " + PORT);
    }

    private void initializeRoutes() {
        server.createContext("/", new Controllers.RootHandler());

        server.createContext("/createSource", new Controllers.CreateSource(infoSecCooker));
        server.createContext("/createSink", new Controllers.CreateSink(infoSecCooker));
        server.createContext("/createHandler", new Controllers.CreateHandler(infoSecCooker));

        server.createContext("/removeSource", new Controllers.RemoveSource(infoSecCooker));
        server.createContext("/removeSink", new Controllers.RemoveSink(infoSecCooker));
        server.createContext("/removeHandler", new Controllers.RemoveHandler(infoSecCooker));

        server.createContext("/createEdge", new Controllers.CreateEdge(infoSecCooker));
        server.createContext("/removeEdge", new Controllers.RemoveEdge(infoSecCooker));
        server.createContext("/runGraph", new Controllers.RunGraph(infoSecCooker));
        server.createContext("/stopGraph", new Controllers.StopGraph(infoSecCooker));

        server.createContext("/getSources", new Controllers.GetSources(infoSecCooker));
        server.createContext("/getSinks", new Controllers.GetSinks(infoSecCooker));
        server.createContext("/getHandlers", new Controllers.GetHandlers(infoSecCooker));
        server.createContext("/getEdges", new Controllers.GetEdges(infoSecCooker));

        // Following methods return the available types
        server.createContext("/node-types", new Controllers.GetNodeTypes());

        server.createContext("/sendGraph", new Controllers.SendGraph(infoSecCooker));
        server.createContext("/checkEdge", new Controllers.CheckEdge(infoSecCooker));
    }
}
