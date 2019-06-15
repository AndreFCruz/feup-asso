package nodes;

import java.util.Map;

public abstract class Node<Id> {

    /**
     * This node's settings.
     * (e.g. name of file to extract from on a FileReaderSink)
     */
    protected Map<String, String> settings;
    /**
     * This node's unique ID.
     */
    private Id id;

    Id initialize(Id id) {
        this.id = id;
        return id;
    }

    public Id getId() {
        return this.id;
    }

    public boolean initializeSettings(Map<String, String> settings) {
        return true;
    }
}
