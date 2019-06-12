package manager;

import api.RESTServer;
import graph.GraphLoader;
import graph.GraphRunnable;
import graph.GraphTopology;
import org.json.JSONObject;

import java.io.IOException;

public class InfoSecCooker {
    public RESTServer restServer;
    public GraphRunnable graph;

    public InfoSecCooker() throws IOException {
        this.restServer = new RESTServer(this);
    }

    public boolean loadGraph(JSONObject graphObj) {
        GraphTopology graphTopology = GraphLoader.loadGraph(graphObj);
        if (graphTopology == null)
            return false;

        this.graph = new GraphRunnable(graphTopology);
        return true;
    }

    public void startGraph() {
        this.graph.stop();
        this.graph.start();
    }

    public void stopGraph() {
        this.graph.stop();
    }
}
