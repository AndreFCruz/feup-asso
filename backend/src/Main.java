import manager.InfoSecCooker;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        InfoSecCooker infoSecCooker = new InfoSecCooker();
        infoSecCooker.initializeGraph();
        infoSecCooker.run();
        infoSecCooker.restServer.start();
    }
}
