package nodes;

import java.util.Map;

public abstract class Node {

    /**
     * This node's unique ID.
     */
    private int id;

    /**
     * This node's settings.
     * (e.g. name of file to extract from on a FileReaderSink)
     */
    private Map<String, String> settings;

    public void initialize(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

}
