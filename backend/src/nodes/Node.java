package nodes;

import java.util.Map;

public abstract class Node {

    /**
     * This node's unique name.
     */
    private String name;

    /**
     * This node's settings.
     * (e.g. name of file to extract from on a FileReaderSink)
     */
    private Map<String, String> settings;

    public String initialize(String name) {
        this.name = name;
        return this.name;
    }

    public String getName() {
        return this.name;
    }

}
