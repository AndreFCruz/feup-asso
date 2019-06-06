import manager.Broker;
import manager.Graph;
import manager.Server;
import nodes.Handler;
import nodes.NodeFactory;
import nodes.Sink;
import nodes.Source;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        Server server = new Server();
        server.initializeGraph();
        server.run();
    }
}
