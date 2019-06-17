import manager.InfoSecCooker;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        InfoSecCooker infoSecCooker = new InfoSecCooker();
        infoSecCooker.initializeDummyGraph();
        infoSecCooker.startGraph();
        infoSecCooker.startServer();
    }
}
