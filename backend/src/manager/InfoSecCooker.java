package manager;

import api.RESTServer;
import graph.GraphLoader;
import graph.GraphRunnable;
import graph.GraphTopology;
import org.json.JSONObject;

import java.io.IOException;

public class InfoSecCooker {
    public GraphRunnable graph;
    private RESTServer restServer;

    public InfoSecCooker() throws IOException {
        this.restServer = new RESTServer(this);
    }

    public void startServer() {
        this.restServer.start();
    }

    public void initializeDummyGraph() {
        initializeGraphRunnable(GraphLoader.loadGraph());
    }

    public boolean loadGraph(JSONObject graphObj) {
        GraphTopology graphTopology = GraphLoader.loadGraph(graphObj);
        if (graphTopology == null)
            return false;

        initializeGraphRunnable(graphTopology);
        return true;
    }

    private void initializeGraphRunnable(GraphTopology graphTopology) {
        if (this.graph != null)
            this.graph.stop();

        this.graph = new GraphRunnable(graphTopology);
    }

    public void startGraph() {
        this.graph.start();
    }

    public void stopGraph() {
        this.graph.stop();
    }
}
