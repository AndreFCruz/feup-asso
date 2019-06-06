import manager.Server;

public class Main {

    public static void main(String[] args) {
        Server server = new Server();
        server.initializeGraph();
        server.run();
    }
}
