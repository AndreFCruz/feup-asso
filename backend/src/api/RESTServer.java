package api;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RESTServer {
    private int PORT = 3000;
    private HttpServer server;

    public RESTServer() throws IOException {
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
    }


}
